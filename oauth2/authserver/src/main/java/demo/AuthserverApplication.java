package demo;

import java.security.KeyPair;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@Controller
@SessionAttributes("authorizationRequest")
@EnableResourceServer
public class AuthserverApplication extends WebMvcConfigurerAdapter {

    public static void main(String[] args) {

        SpringApplication.run(AuthserverApplication.class, args);
    }

    @RequestMapping("/user")
    @ResponseBody
    public Principal user(Principal user) {

        return user;
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/oauth/confirm_access").setViewName("authorize");
    }

    @Bean
    public FilterRegistrationBean someFilterRegistration() {

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new RequestLogFilter());
        registration.addUrlPatterns("/*");
        registration.setName("RequestLogFilter");
        registration.setOrder(1);
        return registration;
    }

    @Configuration
    @Order(-20)
    protected static class LoginConfig extends WebSecurityConfigurerAdapter {


        @Autowired
        private AuthenticationManager authenticationManager;

        @Override
        public void configure(WebSecurity web) throws Exception {

            web.ignoring().mvcMatchers("/oauth/check_token", "/oauth/token_key");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http
                    .formLogin().loginPage("/login").permitAll()
                    .and()
                    .requestMatchers()
                    .antMatchers("/login", "/oauth/authorize"
                            , "/oauth/confirm_access", "/check_token", "/token_key"
                            , "/oauth/check_token", "/oauth/token_key")
                    .and()
                    .authorizeRequests().anyRequest().authenticated().and();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {

            auth.parentAuthenticationManager(authenticationManager);
        }
    }

    @Configuration
    @EnableAuthorizationServer
    protected static class OAuth2AuthorizationConfig extends
            AuthorizationServerConfigurerAdapter {

        @Autowired
        private AuthenticationManager authenticationManager;

        @Bean
        public JwtAccessTokenConverter jwtAccessTokenConverter() {

            JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
            KeyPair keyPair = new KeyStoreKeyFactory(
                    new ClassPathResource("keystore.jks"), "foobar".toCharArray())
                    .getKeyPair("test");
            //new String(Base64.encode(keyPair.getPublic().getEncoded())) //public key generation
            converter.setKeyPair(keyPair);
            return converter;
        }


        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

            clients.inMemory()
                    .withClient("acme")
                    .secret("acmesecret")
                    .autoApprove(false)
                    .accessTokenValiditySeconds(300)
                    .authorizedGrantTypes("authorization_code", "implicit", "refresh_token",
                            "password").scopes("resource-read", "write")
                    .and().withClient("acme2")
                    .secret("acmesecret2")
                    .autoApprove(false)
                    .accessTokenValiditySeconds(300)
                    .authorizedGrantTypes("authorization_code", "implicit", "refresh_token",
                            "password").scopes("resource-read", "write");
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints)
                throws Exception {

            endpoints.authenticationManager(authenticationManager)
                    .accessTokenConverter(jwtAccessTokenConverter());
        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer oauthServer)
                throws Exception {

            oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess(
                    "permitAll()");
        }

    }
}
