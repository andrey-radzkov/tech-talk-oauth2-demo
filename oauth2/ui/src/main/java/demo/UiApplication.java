package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableZuulProxy
@EnableOAuth2Sso
@EnableOAuth2Client
@RestController
public class UiApplication extends WebSecurityConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication.run(UiApplication.class, args);
    }

    @GetMapping("/hello")
    public String response() {
        return "<h1>Hello World</h1>";
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/get-supplier/{id}")
    public Map<String, String> getSupplier(@PathVariable("id") int id) {
        Map<String, String> supplier = new HashMap<>();
        supplier.put("companyName", "name" + id);
        supplier.put("email", "email@user" + id + ".com");
        return supplier;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        http
                .logout().and()
                .authorizeRequests()
                .antMatchers("/index.html", "/home.html", "/", "/login", "/get-supplier/*").permitAll()
                .anyRequest().authenticated()
                .and()
                .csrf()
                .csrfTokenRepository(csrfTokenRepository);
        // @formatter:oln
    }
}

