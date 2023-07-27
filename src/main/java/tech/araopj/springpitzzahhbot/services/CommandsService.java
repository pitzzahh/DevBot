package tech.araopj.springpitzzahhbot.services;

import tech.araopj.springpitzzahhbot.commands.slash_commands.SlashCommand;
import tech.araopj.springpitzzahhbot.commands.chat_commands.ChatCommand;
import tech.araopj.springpitzzahhbot.commands.CommandsConfig;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public record CommandsService(CommandsConfig commandsConfig) {

    public String getRulesCommand() {
        return commandsConfig.getRulesCommand();
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

    public Map<String, SlashCommand> slashCommands() {
        return commandsConfig.getSlashCommands();
    }

}
