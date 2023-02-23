package tech.araopj.springpitzzahhbot.commands.slash_command.commands.joke.entity;

import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.NotNull;

public class Category extends Command.Choice {
    public Category(@NotNull String name, long value) {
        super(name, value);
    }
}
