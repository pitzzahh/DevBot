package tech.araopj.springpitzzahhbot.services.slash_commands;

import tech.araopj.springpitzzahhbot.configs.slash_commands.ConfessionConfig;
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
