package vn.kis.ben10.controllers;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.kis.ben10.models.AccountDTO;
import vn.kis.ben10.models.BankDTO;
import vn.kis.ben10.services.BankService;

import java.util.List;

@RestController
@ResponseBody
@RequestMapping("/napas")
@RequiredArgsConstructor
public class BankController {
    private final BankService service;
    private final List<BankDTO> napas;

    @GetMapping
    public List<BankDTO> getNapasList() {
        return napas;
    }

    @GetMapping("/{bin}/{account}")
    public AccountDTO getAccountName(@Validated @NotBlank @PathVariable @Pattern(regexp = "^[a-zA-Z0-9]{1,25}$") String account,
                                     @Validated @NotNull @PathVariable @Pattern(regexp = "[0-9]{6}") String bin) {
        return service.getAccountName(bin, account);
    }

    @GetMapping("/{bin}/{account}/matching/{name}")
    public AccountDTO validateMatchingName(@Validated @NotBlank @PathVariable @Pattern(regexp = "^[a-zA-Z0-9]{1,25}$") String account,
                                           @Validated @NotNull @PathVariable @Pattern(regexp = "[0-9]{6}") String bin,
                                           @Validated @NotBlank @PathVariable String name) {
        return service.validateMatchingAccountHolderName(bin, account, name);
    }
}
