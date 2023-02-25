package tech.araopj.springpitzzahhbot.commands.slash_command.commands.joke.getJoke.entity;

import net.dv8tion.jda.api.interactions.commands.Command;
import org.springframework.lang.NonNull;

public class Category extends Command.Choice {
    public Category(@NonNull String name, @NonNull String value) {
        super(name, value);
    }
}
