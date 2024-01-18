package vn.kis.ben10.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountDTO {
    private String name;
    private String code;
    private boolean isMatched;
}
