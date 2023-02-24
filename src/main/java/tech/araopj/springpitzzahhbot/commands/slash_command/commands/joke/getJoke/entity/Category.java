package tech.araopj.springpitzzahhbot.commands.slash_command.commands.joke.getJoke.entity;

import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.NotNull;

public class Category extends Command.Choice {
    public Category(@NotNull String name, @NotNull String value) {
        super(name, value);
    }
}
