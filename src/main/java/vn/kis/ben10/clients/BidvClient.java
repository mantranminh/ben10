package vn.kis.ben10.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import vn.kis.ben10.models.BidvDTO;
import vn.kis.ben10.models.BidvGetBanksReqBodyDTO;
import vn.kis.ben10.models.BidvGetName247ReqBodyDTO;
import vn.kis.ben10.models.BidvReqHeaderDTO;

@FeignClient(value = "bidvClient", url = "${feign.client.config.bidvClient.baseURL}")
public interface BidvClient {

    @PostMapping("/getListBank/v002")
    String getBanks(@RequestBody BidvDTO<BidvReqHeaderDTO, BidvGetBanksReqBodyDTO> requestBody);

    @PostMapping("/getName247/v002")
    String getAccountHolderName247(@RequestBody BidvDTO<BidvReqHeaderDTO, BidvGetName247ReqBodyDTO> requestBody);

    @PostMapping("/getNameBidv/v002")
    String getAccountHolderNameInBidvBank(@RequestBody BidvDTO<BidvReqHeaderDTO, BidvGetName247ReqBodyDTO> requestBody);
}
