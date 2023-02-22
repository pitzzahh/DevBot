package tech.araopj.springpitzzahhbot.config.moderation.service;

import tech.araopj.springpitzzahhbot.config.moderation.ModerationConfig;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public record ViolationService(ModerationConfig moderationConfig) {

    /**
     * Adds violation to anyone who says a bad words.
     * @param username the username of the user who violated.
     */
    public void addViolation(final String username) {
        moderationConfig.violations().put(username, getViolationCount(username) + 1);
    }

    private Integer getViolationCount(String username) {
        return moderationConfig.violations()
                .entrySet()
                .stream()
                .filter(e -> e.getKey().equals(username))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(0);
    }

    public boolean violatedThreeTimes(String username) {
        var violated = getViolationCount(username) >= 3;
        if (violated) moderationConfig.violations().remove(username);
        return violated;
    }

    public int getReplyDeletionDelayInMinutes() {
        return moderationConfig.getReplyDeletionDelayInMinutes();
    }

    public int getMessageDeletionDelayInSeconds() {
        return moderationConfig.getMessageDeletionDelayInSeconds();
    }

}
