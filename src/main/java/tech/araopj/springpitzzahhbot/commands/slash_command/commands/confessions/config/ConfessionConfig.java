package tech.araopj.springpitzzahhbot.commands.slash_command.commands.confessions.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import lombok.Getter;
@Getter
@Configuration
public class ConfessionConfig {

    @Value("${bot.channel.confessions.enter-confession-channel.name}")
    private String enterSecretChannel;

    @Value("${bot.channel.confessions.sent-confessions-channel.name}")
    private String sentSecretChannel;

}
