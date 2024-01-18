package vn.kis.ben10.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BidvResHeaderDTO {
    private String requestId;
    private String errorCode;
    private String errorDesc;
    private String secureCode;
}
