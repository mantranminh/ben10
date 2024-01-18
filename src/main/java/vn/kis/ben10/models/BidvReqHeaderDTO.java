package vn.kis.ben10.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BidvReqHeaderDTO {
    private String appCode;
    private String requestId;
    private String username;
    private String password;
    private String secureCode;
}
