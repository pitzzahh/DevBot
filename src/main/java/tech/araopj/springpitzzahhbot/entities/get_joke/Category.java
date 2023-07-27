package tech.araopj.springpitzzahhbot.entities.get_joke;

import net.dv8tion.jda.api.interactions.commands.Command;
import org.springframework.lang.NonNull;

public class Category extends Command.Choice {
    public Category(@NonNull String name, @NonNull String value) {
        super(name, value);
    }
}
