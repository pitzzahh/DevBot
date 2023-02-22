package tech.araopj.springpitzzahhbot.config.moderation;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Getter
@Configuration
public class ModerationConfig {

    @Value("${bot.moderation.reply-deletion-delay-in-minutes}")
    public int replyDeletionDelayInMinutes;

    @Value("${bot.moderation.message-deletion-delay-in-seconds}")
    public int messageDeletionDelayInSeconds;

    @Bean
    public Map<String, Integer> violations() {
        return new HashMap<>();
    }

    @Bean
    public List<String> warnings() {
        return new ArrayList<>();
    }
}
