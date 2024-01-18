package vn.kis.ben10.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BidvGetBanksReqBodyDTO {
    private String serviceId;
    private String merchantId;
    private String merchantName;
    private String channelId;
}

