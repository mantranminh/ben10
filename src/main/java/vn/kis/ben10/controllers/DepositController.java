package vn.kis.ben10.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.kis.ben10.configurations.AppConf;
import vn.kis.ben10.models.KisDepositBank;

import java.util.List;

@RestController
@ResponseBody
@RequestMapping("/deposits")
@RequiredArgsConstructor
public class DepositController {
    private final AppConf conf;

    @GetMapping("/banks")
    public List<KisDepositBank> getKisDepositBanks(@RequestParam(defaultValue = "20", required = false) int limit){
        return conf.getKisDepositBanks().stream()
                .limit(limit)
                .toList();
    }
}
