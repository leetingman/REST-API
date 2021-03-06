package me.liting.restapiwithspring.configs;

import me.liting.restapiwithspring.accounts.Account;
import me.liting.restapiwithspring.accounts.AccountRepository;
import me.liting.restapiwithspring.accounts.AccountRole;
import me.liting.restapiwithspring.accounts.AccountService;
import me.liting.restapiwithspring.common.AppProperties;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class AppConfig {



    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
        //여기서 등록한 빈 컨트롤러에서 사용가능
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        //use prefix hashtype
    }
    @Bean
    public ApplicationRunner applicationRunner(){
        return new ApplicationRunner() {
            @Autowired
            AccountService accountService;

            @Autowired
            AppProperties appProperties;

            @Override
            public void run(ApplicationArguments args) throws Exception {
//                Account liting = Account.builder()
//                        .email("st@gmail.com")
//                        .password("liting")
//                        .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
//                        .build();
//                accountService.saveAccount(liting);

                Account admin = Account.builder()
                        .email(appProperties.getAdminUsername())
                        .password(appProperties.getAdminPassword())
                        .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                        .build();
                accountService.saveAccount(admin);
                Account user = Account.builder()
                        .email(appProperties.getUserUsername())
                        .password(appProperties.getUserPassword() )
                        .roles(Set.of(AccountRole.USER))
                        .build();
                accountService.saveAccount(user);
            }
        };
    }


}
