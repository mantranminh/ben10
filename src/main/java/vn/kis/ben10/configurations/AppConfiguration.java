package vn.kis.ben10.configurations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.kis.ben10.models.BankDTO;
import vn.kis.ben10.services.InitBankInfoService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class AppConfiguration {

    public static final String DEFINED = "DEFINED";

    @Bean(name = "napasList")
    public List<BankDTO> napasList(InitBankInfoService initBankInfoService, AppConf conf) {
        // DEFINED|COMBINED
        log.debug(STR."Napas list init mode \{conf.getInitNapasMode()}");
        List<BankDTO> rs;
        if (DEFINED.equals(conf.getInitNapasMode())) {
            rs = conf.getDefinedNapasList().stream().toList();
        } else { // COMBINED
            rs = initBankInfoService.getNapasBanks();
        }
        return rs;
    }

    @Bean(name = "napasMap")
    public Map<String, BankDTO> napasMap(List<BankDTO> napasList) {
        return napasList.stream()
                .collect(Collectors.toMap(BankDTO::bin, bankDTO -> bankDTO));
    }
}
