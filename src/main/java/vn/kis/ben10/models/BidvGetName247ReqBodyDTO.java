package vn.kis.ben10.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BidvGetName247ReqBodyDTO {
    private String serviceId;
    private String merchantId;
    private String merchantName;
    private String channelId;
    private String credit;
    private String bankCode;
    private String creditType;
}

