package vn.kis.ben10.models;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BankDTO(String bin, String shortName, String name, String logoUrl, String code, String citadCode) {
}
