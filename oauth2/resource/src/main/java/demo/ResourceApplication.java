package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@SpringBootApplication
@RestController
@EnableResourceServer
public class ResourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResourceApplication.class, args);
    }

    @RequestMapping("/")
    public Message home(HttpServletRequest request,
                        HttpServletResponse response) {
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

