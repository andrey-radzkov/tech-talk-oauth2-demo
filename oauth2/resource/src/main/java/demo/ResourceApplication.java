package demo;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableResourceServer
public class ResourceApplication {

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
    public Message home(HttpServletRequest request,
            HttpServletResponse response) {

        LOG.info("Authorization: " + request.getHeader("Authorization"));
        return new Message("Hello World");
    }

    @RequestMapping("/custom")
    public Message custom() {

        return new Message("Hello World");
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

