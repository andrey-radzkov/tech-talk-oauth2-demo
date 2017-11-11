package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
@EnableZuulProxy
@EnableOAuth2Sso
@EnableOAuth2Client
@Controller
public class UiApplication2 extends WebSecurityConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication.run(UiApplication2.class, args);
    }

    @GetMapping("/hello")
    @ResponseBody
    public String response() {
        return "<h1>Hello World</h1>";
    }


    @Override
    public void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        http
                .logout().and()
                .authorizeRequests()
                .antMatchers("/static/js/index.html"
                        , "/static/js/home.html"
                        , "/home.html"
                        , "/"
                        , "/login"
                        , "/implicit"
                        , "/implicit.html").permitAll()
                .anyRequest().authenticated()
                .and()
                .csrf()
                .csrfTokenRepository(csrfTokenRepository);
        // @formatter:oln
    }
}

