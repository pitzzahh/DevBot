package tech.araopj.springpitzzahhbot.commands.service;

import tech.araopj.springpitzzahhbot.commands.CommandsConfig;
import org.springframework.stereotype.Service;

@Service
public record CommandsService(CommandsConfig commandsConfig) {

    public String getVerifyCommand() {
        return commandsConfig.getVerifyCommand();
    }

    public String getConfessCommand() {
        return commandsConfig.getConfessCommand();
    }

    public String memberUpdatesCommand() {
        return commandsConfig.getMemberUpdatesCommand();
    }

    public int messageDeletionDelay() {
        return commandsConfig.getMessageDeletionDelayInMinutes();
    }

    public String getPrefix() {
        return commandsConfig.getPrefix();
    }

}
