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

package tech.araopj.springpitzzahhbot.commands.slash_command.commands.joke.submitJoke;

import tech.araopj.springpitzzahhbot.commands.slash_command.commands.joke.service.JokesService;
import tech.araopj.springpitzzahhbot.commands.slash_command.CommandContext;
import tech.araopj.springpitzzahhbot.utilities.service.MessageUtilService;
import tech.araopj.springpitzzahhbot.commands.slash_command.SlashCommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import tech.araopj.springpitzzahhbot.config.HttpConfig;
import static java.util.concurrent.TimeUnit.MINUTES;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZoneId;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import static java.awt.Color.CYAN;
import static java.awt.Color.YELLOW;
import static java.time.LocalDateTime.now;

@Slf4j
@Component
public record SubmitJoke(
        MessageUtilService messageUtilService,
        JokesService jokesService,
        HttpConfig httpConfig
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

    /**
     * Contains the process to be executed.
     *
     * @param context the command context containing the information about the command.
     */
    private void process(CommandContext context) {

        var url = jokesService.createJokeSubmitUrl();

        log.info("Submit Joke url: {}", url);
        var jokeSubmitBody = jokesService.createJokeSubmitBody(
                context.getEvent().getOption("joke"),
                context.getEvent().getOption("category"),
                context.getEvent().getOption("language")
        );
        log.info("Submit Joke body: {}", jokeSubmitBody);
        final var REQUEST = httpConfig.httpBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jokeSubmitBody))
                .build();

        final HttpResponse<String> RESPONSE;

        try {
            RESPONSE = httpConfig.httpClient().send(REQUEST, HttpResponse.BodyHandlers.ofString());
            log.info("Response from joke api: {}", RESPONSE.body());
        } catch (IOException | InterruptedException e) {
            log.error("Error while sending request to joke api", e);
            throw new RuntimeException(e);
        }

        if (RESPONSE.statusCode() == 200) {
            messageUtilService.getEmbedBuilder()
                    .clear()
                    .clearFields()
                    .setColor(CYAN)
                    .setTitle(RESPONSE.body())
                    .setDescription("Your joke has been sent to the joke api. It will be reviewed and added to the joke api if it is good enough.")
                    .setTimestamp(now(ZoneId.systemDefault()).plusMinutes(messageUtilService.getReplyDeletionDelayInMinutes()))
                    .setFooter("This message will be automatically deleted on");
            context.getEvent()
                    .getInteraction()
                    .replyEmbeds(messageUtilService.getEmbedBuilder().build())
                    .queue(m -> m.deleteOriginal().queueAfter(messageUtilService.getReplyDeletionDelayInMinutes(), MINUTES));
        }
        else if(RESPONSE.statusCode() == 400) {
            messageUtilService.getEmbedBuilder()
                    .clear()
                    .clearFields()
                    .setColor(YELLOW)
                    .setTitle("Failed to send joke to joke api")
                    .setDescription("Your joke is already the same as another joke in the joke api. Please try again with a different joke.")
                    .setTimestamp(now(ZoneId.systemDefault()).plusMinutes(messageUtilService.getReplyDeletionDelayInMinutes()))
                    .setFooter("This message will be automatically deleted on");
            context.getEvent()
                    .getInteraction()
                    .replyEmbeds(messageUtilService.getEmbedBuilder().build())
                    .queue(m -> m.deleteOriginal().queueAfter(messageUtilService.getReplyDeletionDelayInMinutes(), MINUTES));
        }
        else {
            messageUtilService.getEmbedBuilder()
                    .clear()
                    .clearFields()
                    .setColor(YELLOW)
                    .setTitle("Failed to send joke to joke api")
                    .setDescription("I couldn't send your request at the moment😢.")
                    .setTimestamp(now(ZoneId.systemDefault()).plusMinutes(messageUtilService.getReplyDeletionDelayInMinutes()))
                    .setFooter("This message will be automatically deleted on");
            context.getEvent()
                    .getInteraction()
                    .replyEmbeds(messageUtilService.getEmbedBuilder().build())
                    .queue(m -> m.deleteOriginal().queueAfter(messageUtilService.getReplyDeletionDelayInMinutes(), MINUTES));
        }
    }

    /**
     * Supplies the name of the slash command.
     *
     * @return a {@code Supplier<String>}.
     * @see Supplier
     */
    @Override
    public Supplier<String> name() {
        return () -> "submit-joke";
    }

    /**
     * Supplies the command data of a slash command.
     *
     * @return a {@code Supplier<CommandData>}.
     * @see Supplier
     * @see CommandData
     */
    @Override
    public Supplier<CommandData> getCommandData() {
        return () -> new CommandDataImpl(
                name().get(),
                description().get())
                .addOptions(
                        new OptionData(OptionType.STRING, "category", "Category of the joke", true)
                                .setDescription("Select the category of your joke")
                                .addChoices(jokesService.getCategories()),
                        new OptionData(OptionType.STRING, "language", "Language of the joke", true)
                                .setDescription("Select the language of your joke")
                                .addChoices(jokesService.getLanguages()),
                        new OptionData(OptionType.STRING, "joke", "The joke you to submit", true)
                                .setDescription("Enter your joke")
                );
    }

    /**
     * Supplies the description of a slash command.
     *
     * @return a {code Supplier<String>} containing the description of the command.
     * @see Supplier
     */
    @Override
    public Supplier<String> description() {
        return () -> "Submit a joke to the bot";
    }
}
