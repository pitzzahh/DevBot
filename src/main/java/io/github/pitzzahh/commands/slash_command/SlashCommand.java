package io.github.pitzzahh.commands.slash_command;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.io.IOException;

public interface SlashCommand {

    /**
     * Executes a {@code SlashCommand}
     * @return nothing.
     * @see Consumer
     */
    Consumer<CommandContext> execute() throws InterruptedException, IOException;

    /**
     * Supplies the name of the slash command.
     * @return a {@code Supplier<String>}.
     * @see Supplier
     */
    Supplier<String> name();

    /**
     * Supplies the command data of a slash command.
     * @return a {@code Supplier<CommandData>}.
     * @see Supplier
     * @see CommandData
     */
    default Supplier<CommandData> getCommandData() {
        return () -> new CommandDataImpl(name().get(), description().get());
    }

    /**
     * Supplies the description of a slash command.
     * @return a {code Supplier<String>} containing the description of the command.
     * @see Supplier
     */
    Supplier<String> description();
}