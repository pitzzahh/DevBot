/*
 * MIT License
 *
 * Copyright (c) 2022 Peter John Arao
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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package tech.araopj.devbot.commands.chat_commands.commands;

import tech.araopj.devbot.commands.chat_commands.ChatCommandManager;
import tech.araopj.devbot.services.MessageUtilService;
import tech.araopj.devbot.commands.chat_commands.CommandContext;
import tech.araopj.devbot.commands.chat_commands.ChatCommand;
import tech.araopj.devbot.services.CommandsService;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.awt.*;

@Component
public record HelpChatCommand(
        MessageUtilService messageUtilService,
        CommandsService commandsService,
        ChatCommandManager chatCommandManager
) implements ChatCommand {
    /**
     * Contains the process to be handled.
     *
     * @param context a {@code CommandContext}.
     * @see CommandContext
     */
    public void process(CommandContext context) {
        final var contextArgs = context.getArgs();
        final var channel = context.getEvent().getChannel();

        if (contextArgs.isEmpty()) {
            messageUtilService.generateBotSentMessage(
                    context.getEvent(),
                    Color.YELLOW,
                    "List of Chat Commands",
                    "List of available chat commands (slash commands are also available)",
                    LocalDateTime.now(ZoneId.of("UTC")),
                    format("Created by %s", context.getGuild().getJDA().getSelfUser().getAsTag())
            );
            for (ChatCommand chatCommand : commandsService.chatCommands()) {
                messageUtilService
                        .getEmbedBuilder()
                        .addField(commandsService.getPrefix().concat(chatCommand.name().get()), chatCommand.description().get(), true);
            }
            channel.sendMessageEmbeds(messageUtilService.getEmbedBuilder().build()).queue();
            return;
        }
        final var chatCommandByName = contextArgs.get(0);
        final var COMMAND = chatCommandManager.getChatCommandByName(chatCommandByName);
        if (COMMAND.isEmpty()) {
            messageUtilService.generateBotSentMessage(
                    context.getEvent(),
                    Color.YELLOW.brighter(),
                    format("No chat_command found for %s", chatCommandByName),
                    null,
                    LocalDateTime.now(ZoneId.of("UTC")),
                    format("Created by %s", context.getGuild().getJDA().getSelfUser().getAsTag())
            );
        } else {
            messageUtilService.generateBotSentMessage(
                    context.getEvent(),
                    Color.CYAN.brighter(),
                    COMMAND.get().name().get(),
                    COMMAND.get().description().get(),
                    LocalDateTime.now(ZoneId.of("UTC")),
                    format("Created by %s", context.getGuild().getJDA().getSelfUser().getAsTag())
            );
        }
        channel.sendMessageEmbeds(messageUtilService.getEmbedBuilder().build()).queue();
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
                "Usage: ".concat(commandsService.getPrefix()).concat("help [chat_command]");
    }

    /**
     * The possible aliases for a chat_command.
     *
     * @return a {@code List<String>} containing the aliases of a chat_command.
     */
    @Override
    public Supplier<List<String>> aliases() {
        return () -> List.of("commands", "chat commands", "chat_command list", "com");
    }
}
