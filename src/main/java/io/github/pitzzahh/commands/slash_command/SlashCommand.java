package io.github.pitzzahh.commands.slash_command;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

public interface SlashCommand {

    void execute(CommandContext context);

    String name();

    default CommandData getInfo() {
        return new CommandDataImpl(name(), description());
    }

    String description();
}