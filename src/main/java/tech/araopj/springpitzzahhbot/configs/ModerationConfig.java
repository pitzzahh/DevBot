package tech.araopj.springpitzzahhbot.configs;

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
