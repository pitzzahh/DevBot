package io.github.pitzzahh.commands.slash_command;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface SlashCommand {

    Consumer<CommandContext> execute();


    Supplier<String> name();

    default Supplier<CommandData> getInfo() {
        return () -> new CommandDataImpl(name().get(), description().get());
    }


    Supplier<String> description();
}