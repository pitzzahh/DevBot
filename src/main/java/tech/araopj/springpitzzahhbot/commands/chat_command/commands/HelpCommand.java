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
package tech.araopj.springpitzzahhbot.commands.chat_command.commands;
import tech.araopj.springpitzzahhbot.commands.chat_command.CommandContext;
import tech.araopj.springpitzzahhbot.commands.chat_command.CommandManager;
import tech.araopj.springpitzzahhbot.commands.chat_command.Command;
import tech.araopj.springpitzzahhbot.commands.CommandsConfig;
import tech.araopj.springpitzzahhbot.utilities.MessageUtil;
import org.springframework.stereotype.Component;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.List;
import java.awt.*;

@Component
public record HelpCommand(
        CommandsConfig commandsConfig,
        CommandManager MANAGER,
        MessageUtil messageUtil

) implements Command {
    /**
     * Contains the process to be handled.
     *
     * @param context a {@code CommandContext}.
     * @see CommandContext
     */
    public void process(CommandContext context) {
        final var ARGS = context.getArgs();
        final var CHANNEL = context.getEvent().getChannel();

        if (ARGS.isEmpty()) {
            messageUtil.getEmbedBuilder().clear()
                    .clearFields()
                    .setColor(Color.BLUE)
                    .setTitle("List of Commands")
                    .setFooter("Created by pitzzahh-bot#3464", context.getGuild().getIconUrl());
            MANAGER.getCOMMANDS()
                    .forEach(
                            c -> messageUtil.getEmbedBuilder()
                                    .addField(
                                            commandsConfig.getPrefix().concat(c.name().get()),
                                            c.description().get(),
                                            true
                                    )
                    );
            CHANNEL.sendMessageEmbeds(messageUtil.getEmbedBuilder().build()).queue();
            return;
        }
        final var SEARCH = ARGS.get(0);
        final var COMMAND = MANAGER.getCommand.apply(SEARCH);
        if (COMMAND.isEmpty()) CHANNEL.sendMessageFormat("Nothing found on chat_command: %s", SEARCH).queue();
        else {
            messageUtil.getEmbedBuilder().clear()
                    .clearFields()
                    .setColor(Color.CYAN.brighter())
                    .setTitle(COMMAND.get().name().get())
                    .setDescription(COMMAND.get().description().get())
                    .setFooter("Created by pitzzahh-bot#3464", context.getGuild().getIconUrl());
            CHANNEL.sendMessageEmbeds(messageUtil.getEmbedBuilder().build()).queue();
        }
    }

    /**
     * Handles the chat_command.
     * Accepts a {@code CommandContext}.
     *
     * @see CommandContext
     */
    @Override
    public Consumer<CommandContext> handle() {
        return this::process;
    }

    /**
     * The name of the chat_command.
     *
     * @return the name of the chat_command.
     */
    @Override
    public Supplier<String> name() {
        return () -> "help";
    }

    /**
     * The description of the chat_command.
     *
     * @return the description of the chat_command.
     */
    @Override
    public Supplier<String> description() {
        return () -> "Shows the list of commands in the bot\n" +
                     "Usage: ".concat(commandsConfig.getPrefix()).concat("help [chat_command]");
    }

    /**
     * The possible aliases for a chat_command.
     *
     * @return a {@code List<String>} containing the aliases of a chat_command.
     */
    @Override
    public Supplier<List<String>> aliases() {
        return () -> List.of("commands", "chat_command", "chat_command list", "com");
    }
}
