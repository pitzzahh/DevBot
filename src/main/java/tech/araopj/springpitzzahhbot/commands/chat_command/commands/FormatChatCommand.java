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

import static net.dv8tion.jda.api.interactions.components.buttons.Button.primary;
import tech.araopj.springpitzzahhbot.services.MessageUtilService;
import tech.araopj.springpitzzahhbot.commands.chat_command.CommandContext;
import static net.dv8tion.jda.api.interactions.components.ActionRow.of;
import tech.araopj.springpitzzahhbot.commands.chat_command.ChatCommand;
import tech.araopj.springpitzzahhbot.services.CommandsService;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;
import java.util.function.Consumer;
import java.util.function.Supplier;
import static java.awt.Color.RED;

@Component
public record FormatChatCommand(
        MessageUtilService messageUtilService,
        CommandsService commandsService
) implements ChatCommand {

    /**
     * Contains the process to be handled.
     * @param context a {@code CommandContext}.
     * @see CommandContext
     */
    public void process(@NonNull CommandContext context) {
        final var contextArgs = context.getArgs();
        final var channel = context.getEvent().getChannel();

        if (contextArgs.size() < 2) {
            final var BUTTON = primary("ok", "okay");

            messageUtilService.generateAutoDeleteMessage(
                    context.event(),
                    RED,
                    "Missing Content",
                    "Please provide the language and the content to format."
            );

            messageUtilService
                    .getMessageBuilder()
                    .clear()
                    .setEmbeds(messageUtilService.getEmbedBuilder().build())
                    .setActionRows(of(BUTTON));

            channel.sendMessage(messageUtilService.getMessageBuilder().build()).queue();
            return;
        }
        final var language = contextArgs.get(0);
        final var contentRaw = context.getEvent().getMessage().getContentRaw();
        final var index = contentRaw.indexOf(language) + language.length();
        final var content = contentRaw.substring(index).trim();
        context.getEvent()
                .getMessage()
                .reply("```" + language + "\n" + content + "```")
                .queue(
                        message -> context.event()
                                .getMessage()
                                .delete()
                                .queue()
                );
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
        return () -> "format";
    }

    /**
     * The description of the chat_command.
     *
     * @return the description of the chat_command.
     */
    @Override
    public Supplier<String> description() {
        return () -> "Formats a code.\n" +
                "Usage: ".concat(commandsService.getPrefix().concat(name().get())).concat(" [language] [content]");
    }
}
