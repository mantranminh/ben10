package vn.kis.ben10.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import vn.kis.ben10.clients.BidvClient;
import vn.kis.ben10.clients.TtlClient;
import vn.kis.ben10.configurations.AppConf;
import vn.kis.ben10.models.*;
import vn.kis.ben10.utils.Utils;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InitBankInfoService {
    private static final String TTL_SUCCESS = "OLS0000";
    private static final String BIDV_SUCCESS = "000";
    private static final int SHORT_CITAD_LENGTH = 3;
    private static final int FULL_CITAD_LENGTH = 8;
    public static final String BIDB_BIN = "970418";
    private final BidvClient bidvClient;
    private final TtlClient ttlClient;
    private final AppConf conf;
    private final Gson gson;

    public List<BankDTO> getNapasBanks() {
        // Get from BIDV
        String rs = bidvClient.getBanks(getBidvGetBanksRequestBody());
        log.debug(STR."getNapasBanks(): BIDV res \{rs}");
        BidvMsgDTO<BidvResHeaderDTO, BidvGetBanksResBodyDTO> r = gson.fromJson(rs,
                TypeToken.getParameterized(BidvMsgDTO.class, BidvResHeaderDTO.class, BidvGetBanksResBodyDTO.class)
                        .getType());
        if (!BIDV_SUCCESS.equals(r.getHeader().getErrorCode())) {
            log.error("Get BIDV banks failed: {}-{}", r.getHeader().getErrorCode(), r.getHeader().getErrorDesc());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "BIDV response error");
        }
        Map<String, BidvBankDTO> napas = r.getBody().getListBank().stream()
                .filter(bidvBankDTO -> StringUtils.hasText(bidvBankDTO.getBankCodeIbft()))
                .collect(Collectors.toMap(BidvBankDTO::getBankCodeCitad, bidvBankDTO -> bidvBankDTO));

        BankDTO bidv =
                conf.getDefinedNapasList().stream()
                        .collect(Collectors.toMap(BankDTO::bin, bankDTO -> bankDTO))
                        .get(BIDB_BIN);
        napas.putIfAbsent(extractCitadCode(bidv.citadCode()), BidvBankDTO.builder()
                .bankCodeCitad(bidv.citadCode())
                .bankCodeIbft(bidv.bin())
                .bankName(bidv.name())
                .build());

        // Get from TTL
        TtlGetOperatorTokenResDTO tokenResDTO =
                ttlClient.getOperatorToken(new TtlGetOperatorTokenReqDTO(conf.getTtlOperatorUsername(), conf.getTtlOperatorPassword()));
        if (!TTL_SUCCESS.equals(tokenResDTO.getErrorCode())) {
            log.error("Get TTL operator token failed: {}-{}", tokenResDTO.getErrorCode(), tokenResDTO.getErrorMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Tll response error");
        }
        TtlDefinedBanksResDTO definedBanksResDTO =
                ttlClient.getDefinedBanks(new TtlDefinedBanksReqDTO(tokenResDTO.getTokenID(), tokenResDTO.getOperatorID()));
        if (!TTL_SUCCESS.equals(definedBanksResDTO.getErrorCode())) {
            log.error("Get TTL defined banks failed: {}-{}", definedBanksResDTO.getErrorCode(), definedBanksResDTO.getErrorMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Tll response error");
        }

        // Base on what TTL defined, filter, then add-on bin
        return definedBanksResDTO.getMainResult().stream()
                .filter(ttlBankDTO -> napas.containsKey(extractCitadCode(ttlBankDTO.getBankBranch())))
                .map(ttlBankDTO -> convert(ttlBankDTO, napas.get(extractCitadCode(ttlBankDTO.getBankBranch()))))
                .toList();
    }

    /**
     * As BIDV recommend citad code for length 3 (no need province and branch ID anymore, this function to adapt this
     *
     * @param ttlDefinedCitadCode - Which is defined in core, has length 3 or 8. Require not null
     * @return 3 digits for citad code only
     */
    private static String extractCitadCode(@NotNull String ttlDefinedCitadCode) {
        if (ttlDefinedCitadCode.length() == SHORT_CITAD_LENGTH) {
            return ttlDefinedCitadCode;
        } else if (ttlDefinedCitadCode.length() == FULL_CITAD_LENGTH) {
            return ttlDefinedCitadCode.substring(2, 5);
        } else {
            log.error("TTL defined citad core error: {}", ttlDefinedCitadCode);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Tll defined citad error");
        }
    }

    private BankDTO convert(TtlBankDTO ttlBankDTO, BidvBankDTO bidvBankDTO) {
        return new BankDTO(bidvBankDTO.getBankCodeIbft(),
                null,
                ttlBankDTO.getBankBranchDesc(),
                null,
                ttlBankDTO.getBankID(),
                ttlBankDTO.getBankBranch()
        );
    }

    private BidvDTO<BidvReqHeaderDTO, BidvGetBanksReqBodyDTO> getBidvGetBanksRequestBody() {
        BidvMsgDTO<BidvReqHeaderDTO, BidvGetBanksReqBodyDTO> msg = new BidvMsgDTO<>();
        msg.setBody(BidvGetBanksReqBodyDTO.builder()
                .serviceId(conf.getBidvServiceId())
                .merchantId(conf.getBidvMerchantId())
                .merchantName(conf.getBidvMerchantName())
                .channelId(conf.getBidvChannelId())
                .build());
        msg.setHeader(BidvReqHeaderDTO.builder()
                .appCode(conf.getBidvAppCode())
                .username(conf.getBidvUsername())
                .password(conf.getBidvPassword())
                .requestId(UUID.randomUUID().toString())
                .build());
        msg.getHeader().setSecureCode(getSecurityCodeForBidvGetBanks(msg));
        BidvDTO<BidvReqHeaderDTO, BidvGetBanksReqBodyDTO> r = new BidvDTO<>();
        r.setMsg(msg);
        return r;
    }

    private String getSecurityCodeForBidvGetBanks(BidvMsgDTO<BidvReqHeaderDTO, BidvGetBanksReqBodyDTO> msg) {
        // secureCode=MD5(privatekey|requestId|serviceId|merchantId|merchantName|channelId|extraInfo1|extraInfo2|extraInfo3|extraInfo4|extraInfo5)
        String securityCodeInPlainText =
                STR."\{conf.getBidvSecret()}|\{msg.getHeader().getRequestId()}|\{msg.getBody().getServiceId()}|\{msg.getBody().getMerchantId()}|\{msg.getBody().getMerchantName()}|\{msg.getBody().getChannelId()}|||||";
        String securityCodeDigest = Utils.getMd5Hash(securityCodeInPlainText);
        log.debug("getSecurityCodeForBidvGetBanks(): {} to {}", securityCodeInPlainText, securityCodeDigest);
        return securityCodeDigest;
    }
}
