package tech.araopj.springpitzzahhbot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import lombok.Getter;

@Getter
@Configuration
public class TokenConfig {

    @Value("${bot.token}")
    private String token;

}
