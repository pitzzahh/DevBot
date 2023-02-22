package tech.araopj.springpitzzahhbot.commands.slash_command.commands.confessions.service;

import tech.araopj.springpitzzahhbot.commands.slash_command.commands.confessions.config.ConfessionConfig;
import org.springframework.stereotype.Service;

@Service
public record ConfessionService(ConfessionConfig confessionConfig) {

    public String enterSecretChannelName() {
        return confessionConfig.getEnterSecretChannel();
    }

    public String sentSecretChannelName() {
        return confessionConfig.getSentSecretChannel();
    }


}
