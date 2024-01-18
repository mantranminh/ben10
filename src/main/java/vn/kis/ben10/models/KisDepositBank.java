package vn.kis.ben10.models;

import lombok.Data;

@Data
public class KisDepositBank {
    private String name;
    private String shortName;
    private String code;
    private String bin;
    private String accNumber;
    private String accHolder;
//    private String logoUrl;
    private String remark = "{subAccountID} {fullName}";
}
