package tech.araopj.springpitzzahhbot.config.moderation;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import java.util.*;

@Configuration
public class ModerationConfig {
    @Bean
    public Map<String, Integer> violations() {
        return new HashMap<>();
    }

    @Bean
    public List<String> warnings() {
        return new ArrayList<>();
    }
}
