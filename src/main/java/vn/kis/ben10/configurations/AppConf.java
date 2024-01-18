package vn.kis.ben10.configurations;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import vn.kis.ben10.models.BankDTO;
import vn.kis.ben10.models.KisDepositBank;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.application")
public class AppConf {
    private String name;
    private String ttlOperatorUsername;
    private String ttlOperatorPassword;
    private String bidvServiceId;
    private String bidvMerchantId;
    private String bidvMerchantName;
    private String bidvChannelId;
    private String bidvAppCode;
    private String bidvUsername;
    private String bidvPassword;
    private String bidvSecret;
    private boolean skipCheckNameEnabled;
    private List<KisDepositBank> kisDepositBanks;
    private String initNapasMode;
    private List<BankDTO> definedNapasList;
}
