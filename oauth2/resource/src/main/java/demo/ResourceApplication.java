package demo;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@SpringBootApplication
@RestController
@Configuration
@EnableResourceServer
public class ResourceApplication extends ResourceServerConfigurerAdapter {

    private static Logger LOG = Logger.getLogger(ResourceApplication.class);

    @Value("${security.oauth2.resource.tokenInfoUri:}")
    private String tokenInfoUri;

    public static void main(String[] args) {

        SpringApplication.run(ResourceApplication.class, args);
    }

    @Bean
    public RemoteTokenServices tokenService() {

        if (!StringUtils.isEmpty(tokenInfoUri)) {
            RemoteTokenServices tokenService = new RemoteTokenServices();
            tokenService.setCheckTokenEndpointUrl(tokenInfoUri);
            tokenService.setClientId("acme");
            tokenService.setClientSecret("acmesecret");
            return tokenService;
        } else {
            return null;
        }
    }

    @RequestMapping("/")
    public Message home(HttpServletRequest request, HttpServletResponse response) {

        LOG.info("Authorization: " + request.getHeader("Authorization"));
        return new Message("Hello World");
    }

    @RequestMapping("/custom")
    public Message custom() {

        return new Message("Hello World");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .requestMatchers().antMatchers("/**")
                .and()
                .authorizeRequests().anyRequest().access("#oauth2.hasScope('resource-read')");
        // @formatter:on
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("resource-id1");
    }

    class Message {

        private String id = UUID.randomUUID().toString();
        private String content;

        Message() {

        }

        public Message(String content) {

            this.content = content;
        }

        public String getId() {

            return id;
        }

        public String getContent() {

            return content;
        }
    }


}

