package vn.kis.ben10.models;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BidvBankDTO {
    // length 3: without province ID and branch ID
    private String bankCodeCitad;
    private String bankCodeIbft;
    private String bankName;
}

