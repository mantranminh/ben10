package vn.kis.ben10.clients;

import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import vn.kis.ben10.models.TtlDefinedBanksReqDTO;
import vn.kis.ben10.models.TtlDefinedBanksResDTO;
import vn.kis.ben10.models.TtlGetOperatorTokenReqDTO;
import vn.kis.ben10.models.TtlGetOperatorTokenResDTO;

@FeignClient(value = "ttlClient", url = "${feign.client.config.ttlClient.baseURL}")
public interface TtlClient {

    @Retry(name = "throwingException1")
    @PostMapping("/operatorLogin")
    TtlGetOperatorTokenResDTO getOperatorToken(@RequestBody TtlGetOperatorTokenReqDTO requestBody);

    @Retry(name = "throwingException1")
    @PostMapping("/services/eqt/listbankbranch")
    TtlDefinedBanksResDTO getDefinedBanks(@RequestBody TtlDefinedBanksReqDTO requestBody);
}