package vn.kis.ben10.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TtlBankDTO {
    private String bankID;
    private String bankBranch;
    private String bankBranchDesc;
}