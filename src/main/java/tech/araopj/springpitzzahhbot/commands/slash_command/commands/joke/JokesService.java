package tech.araopj.springpitzzahhbot.commands.slash_command.commands.joke;

import tech.araopj.springpitzzahhbot.commands.slash_command.commands.joke.entity.Category;
import org.springframework.stereotype.Service;
import java.util.Collection;

@Service
public record JokesService() {

    public Collection<Category> getCategories() {
        return null;
    }

}
