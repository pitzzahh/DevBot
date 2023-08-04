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

package tech.araopj.devbot.commands.slash_commands.commands.joke;

import tech.araopj.devbot.services.slash_commands.JokesService;
import tech.araopj.devbot.commands.slash_commands.CommandContext;
import tech.araopj.devbot.services.MessageUtilService;
import tech.araopj.devbot.commands.slash_commands.SlashCommand;
import org.springframework.stereotype.Component;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import static java.lang.String.format;

@Slf4j
@Component
public record ViewSubmittedJokes(
        MessageUtilService messageUtilService,
        JokesService jokesService
) implements SlashCommand {
    /**
     * Executes a {@code SlashCommand}
     *
     * @return nothing.
     * @see Consumer
     */
    @Override
    public Consumer<CommandContext> execute() {
        return this::process;
    }

    private void process(CommandContext context) {

        messageUtilService.generateBotSentMessage(
                context.getEvent(),
                Color.YELLOW,
                "List of Submitted Jokes",
                "Select the id of the joke to be approved",
                LocalDateTime.now(ZoneId.of("UTC")),
                format("Created by %s", context.getGuild().getJDA().getSelfUser().getAsTag())
        );
        jokesService.getSubmittedJokes()
                .forEach(joke -> messageUtilService
                        .getEmbedBuilder()
                        .addField(String.valueOf(joke.id()), joke.joke(), true));
        context.getEvent()
                .getInteraction()
                .replyEmbeds(messageUtilService.getEmbedBuilder().build())
                .setEphemeral(true)
                .queue(m -> m.deleteOriginal().queueAfter(messageUtilService.getReplyDeletionDelayInMinutes(), TimeUnit.MINUTES));
    }

    /**
     * Supplies the name of the slash command.
     *
     * @return a {@code Supplier<String>}.
     * @see Supplier
     */
    @Override
    public Supplier<String> name() {
        return "view-submitted-jokes"::toString;
    }

    /**
     * Supplies the description of a slash command.
     *
     * @return a {code Supplier<String>} containing the description of the command.
     * @see Supplier
     */
    @Override
    public Supplier<String> description() {
        return "View all the jokes submitted by the users"::toString;
    }
}
