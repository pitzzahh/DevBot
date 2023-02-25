package tech.araopj.springpitzzahhbot.commands.service;

import tech.araopj.springpitzzahhbot.commands.slash_command.SlashCommand;
import tech.araopj.springpitzzahhbot.commands.chat_command.ChatCommand;
import tech.araopj.springpitzzahhbot.commands.CommandsConfig;
import org.springframework.stereotype.Service;
import java.util.List;

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

    public String getPrefix() {
        return commandsConfig.getPrefix();
    }

    public List<ChatCommand> chatCommands() {
        return commandsConfig.getChatCommands();
    }

    public List<SlashCommand> slashCommands() {
        return commandsConfig.getSlashCommands();
    }

}
