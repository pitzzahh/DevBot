/*
 * MIT License
 *
 * Copyright (c) 2022 pitzzahh
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package tech.araopj.springpitzzahhbot.commands.chat_command;

import org.springframework.beans.factory.annotation.Autowired;
import tech.araopj.springpitzzahhbot.commands.chat_command.commands.FormatCommand;
import tech.araopj.springpitzzahhbot.commands.chat_command.commands.HelpCommand;
import tech.araopj.springpitzzahhbot.commands.chat_command.commands.PingCommand;
import tech.araopj.springpitzzahhbot.exceptions.CommandAlreadyExistException;
import tech.araopj.springpitzzahhbot.commands.CommandsConfig;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import tech.araopj.springpitzzahhbot.utilities.MessageUtil;
import org.springframework.stereotype.Component;
import org.jetbrains.annotations.NotNull;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Arrays;
import java.util.List;

@Component
public class CommandManager {

    private final List<Command> COMMANDS = new ArrayList<>();
    private final CommandsConfig commandsConfig;

    @Autowired
    public CommandManager(CommandsConfig commandsConfig, MessageUtil messageUtil) {
        this.commandsConfig = commandsConfig;
        addCommands(
                new PingCommand(),
                new FormatCommand(commandsConfig, messageUtil),
                new HelpCommand(commandsConfig,this, messageUtil)
        );
    }

    /**
     * Adds a chat_command.
     * @param command the chat_command to add.
     */
    private void addCommand(Command command) {
        var found = COMMANDS.stream()
                .anyMatch(c -> c.name().get().equalsIgnoreCase(command.name().get()));
        if (found) throw new CommandAlreadyExistException("A Command With this name is already present!");
        COMMANDS.add(command);
    }

    private void addCommands(@NotNull Command... command) {
        Arrays.stream(command).forEach(this::addCommand);
    }

    /**
     * Gets a chat_command from the list.
     * accepts a String the name of the chat_command
     * returns a {@code Command}.
     */
    public Function<String, Optional<Command>> getCommand = command -> COMMANDS
            .stream()
            .filter(c -> c.name().get().equalsIgnoreCase(command.toLowerCase()) || c.aliases().get().contains(command.toLowerCase()))
            .findAny();

    /**
     * Handles commands.
     * @param event the event that happened.
     */
    public void handle(@NotNull MessageReceivedEvent event) {
        final var SPLIT = event.getMessage().getContentRaw()
                .replaceFirst("(?i)".concat(Pattern.quote(commandsConfig.getPrefix())), "")
                .split("\\s+");
        final var INVOKED = SPLIT[0].toLowerCase();
        final var COMMAND = getCommand.apply(INVOKED);
        event.getChannel().sendTyping().queue();
        final var ARGS = Arrays.asList(SPLIT).subList(1, SPLIT.length);
        final var COMMAND_CONTEXT = new CommandContext(event, ARGS);
        COMMAND.ifPresentOrElse(command -> command.handle().accept(COMMAND_CONTEXT),
                () -> event.getMessage().reply(String.format("%s is not a chat_command", INVOKED)).queue(
                e -> e.getChannel().sendMessage(";help").queue(m -> m.delete().queue())
        ));
    }

    /**
     * Gets all the commands.
     * @return a {@code List<Command>}.
     */
    public List<Command> getCOMMANDS() {
        return COMMANDS;
    }
}
