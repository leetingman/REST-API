package me.liting.restapiwithspring.configs;

import me.liting.restapiwithspring.accounts.Account;
import me.liting.restapiwithspring.accounts.AccountRole;
import me.liting.restapiwithspring.accounts.AccountService;
import me.liting.restapiwithspring.common.AppProperties;
import me.liting.restapiwithspring.common.BaseControllerTest;
import me.liting.restapiwithspring.common.TestDescription;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//OAuth2 Security
public class AuthServerConfigTest extends BaseControllerTest {

    @Autowired
    AccountService accountService;

    @Autowired
    AppProperties appProperties;

    /*
            grant Type
          1. password
          The Password grant type is a way to exchange a user's credentials for an access token.
          Because the client application has to collect the user's password and send it to the authorization server,
          it is not recommended that this grant be used at all anymore.


          2. refresh token
     */

    @Test
    @TestDescription("인증 토큰을 발급 받는 테스트")
    public void getAuthToken() throws Exception{
        //Given
//        Account liting = Account.builder()
//                .email(appProperties.getUserUsername())
//                .password(appProperties.getUserPassword())
//                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
//                .build();
//        this.accountService.saveAccount(liting);


        this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(appProperties.getClientId(),appProperties.getClientSecret()))
                .param("username",appProperties.getAdminUsername())
                .param("password",appProperties.getAdminPassword())
                .param("grant_type","password")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists());

    }

}