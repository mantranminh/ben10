package vn.kis.ben10.models;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BidvGetBanksResBodyDTO {
    private List<BidvBankDTO> listBank;
}

