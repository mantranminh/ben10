package vn.kis.ben10.models;

import lombok.Data;

import java.util.List;

@Data
public class TtlDefinedBanksResDTO {
    private String errorCode;
    private String errorMessage;
    private List<TtlBankDTO> mainResult;
}