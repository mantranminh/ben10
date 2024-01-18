package vn.kis.ben10.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import vn.kis.ben10.clients.BidvClient;
import vn.kis.ben10.configurations.AppConf;
import vn.kis.ben10.models.*;
import vn.kis.ben10.utils.Utils;

import java.text.Normalizer;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankService {
    public static final String BIDV_SUCCESS = "000";
    private final BidvClient bidvClient;
    private final AppConf conf;
    private final Gson gson;
    private final Map<String, BankDTO> napasMap;

    private BidvDTO<BidvReqHeaderDTO, BidvGetName247ReqBodyDTO> getBidvGetName247RequestBody(String bin, String account) {
        BidvMsgDTO<BidvReqHeaderDTO, BidvGetName247ReqBodyDTO> msg = new BidvMsgDTO<>();
        msg.setBody(BidvGetName247ReqBodyDTO.builder()
                .serviceId(conf.getBidvServiceId())
                .merchantId(conf.getBidvMerchantId())
                .merchantName(conf.getBidvMerchantName())
                .channelId(conf.getBidvChannelId())
                .bankCode(bin)
                .credit(account)
                .creditType("0")
                .build());
        msg.setHeader(BidvReqHeaderDTO.builder()
                .appCode(conf.getBidvAppCode())
                .username(conf.getBidvUsername())
                .password(conf.getBidvPassword())
                .requestId(UUID.randomUUID().toString())
                .build());
        msg.getHeader().setSecureCode(getSecurityCodeForBidvAccountHolderName(msg));
        BidvDTO<BidvReqHeaderDTO, BidvGetName247ReqBodyDTO> r = new BidvDTO<>();
        r.setMsg(msg);
        return r;
    }

    private String getSecurityCodeForBidvAccountHolderName(BidvMsgDTO<BidvReqHeaderDTO, BidvGetName247ReqBodyDTO> msg) {
        // secureCode=MD5(privatekey|requestId|serviceId|merchantId|merchantName|channelId|payerId|credit|creditType|bankCode|extraInfo1|extraInfo2|extraInfo3|extraInfo4|extraInfo5)
        String securityCodeInPlainText =
                STR."\{conf.getBidvSecret()}|\{msg.getHeader().getRequestId()}|\{msg.getBody().getServiceId()}|\{msg.getBody().getMerchantId()}|\{msg.getBody().getMerchantName()}|\{msg.getBody().getChannelId()}||\{msg.getBody().getCredit()}|\{msg.getBody().getCreditType()}|\{msg.getBody().getBankCode()}|||||";
        String securityCodeDigest = Utils.getMd5Hash(securityCodeInPlainText);
        log.debug("getSecurityCodeForBidvAccountHolderName(): {} to {}", securityCodeInPlainText, securityCodeDigest);
        return securityCodeDigest;
    }

    public AccountDTO getAccountName(String bin, String account) {
        // validate
        if (!napasMap.containsKey(bin)) {
            log.warn("getAccountName(): BIN not found {}", bin);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bin not found");
        }

        String rs;
        if ("970418".equals(bin)) { // get name from BIDV
            rs = bidvClient.getAccountHolderNameInBidvBank(getBidvGetName247RequestBody(bin, account));
        } else {
            rs = bidvClient.getAccountHolderName247(getBidvGetName247RequestBody(bin, account));
        }
        JsonObject jsonObject = gson.fromJson(rs, JsonObject.class);
        BidvMsgDTO<BidvResHeaderDTO, BidvGetName247ResBodyDTO> r =
                gson.fromJson(jsonObject.getAsJsonObject("msg"),
                        TypeToken.getParameterized(BidvMsgDTO.class, BidvResHeaderDTO.class, BidvGetName247ResBodyDTO.class).getType());
        String errorCode = r.getHeader().getErrorCode();
        if (!BIDV_SUCCESS.equals(errorCode)) {
            log.warn("getAccountName(): BIDV response error {}-{}: {}/{}", errorCode, r.getHeader().getErrorDesc(), bin, account);
        }
        return AccountDTO.builder()
                .name(r.getBody().getCreditName())
                .code(errorCode)
                .build();
    }

    public AccountDTO validateMatchingAccountHolderName(String bin, String account, String nameOnIdCard) {
        if (conf.isSkipCheckNameEnabled()) {
            log.warn("validateMatchingAccountHolderName(): skip check name enabled");
            return AccountDTO.builder()
                    .name(removeAccents(nameOnIdCard.trim().toUpperCase()))
                    .code(BIDV_SUCCESS)
                    .isMatched(true)
                    .build();
        }

        AccountDTO rs = getAccountName(bin, account);
        if (!BIDV_SUCCESS.equals(rs.getCode())) {
            rs.setMatched(false);
            rs.setName(null);
            return rs;
        }
        // comparison: As bank transfer required, exactly comparison
        String nameOnIdCardNormalized = removeAccents(nameOnIdCard.trim().toUpperCase());
        String accountHolderNameNormalized = removeAccents(rs.getName().trim().toUpperCase());
        log.debug("validateMatchingAccountHolderName(): exactly matching comparison {} vs {}", nameOnIdCardNormalized, accountHolderNameNormalized);
        if (!nameOnIdCardNormalized.equals(accountHolderNameNormalized)) {
            log.warn("validateMatchingAccountHolderName(): name NOT match {}/{}", nameOnIdCardNormalized, accountHolderNameNormalized);
            rs.setMatched(false);
            rs.setName(null);
            return rs;
        }

        rs.setMatched(true);
        return rs;
    }

    /**
     * @param input - Uppercase required
     * @return - normalized
     */
    private static String removeAccents(String input) {
        String regex = "\\p{InCombiningDiacriticalMarks}+";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return Pattern.compile(regex, Pattern.UNICODE_CHARACTER_CLASS).matcher(normalized).replaceAll("")
                .replace("Đ", "D")
                .replace(" ", "");
    }
}
