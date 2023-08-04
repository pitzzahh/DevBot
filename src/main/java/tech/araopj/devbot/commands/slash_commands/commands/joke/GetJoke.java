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
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import com.fasterxml.jackson.core.JsonProcessingException;
import tech.araopj.devbot.configs.HttpConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import static java.time.LocalDateTime.now;
import static java.lang.String.format;
import static java.awt.Color.YELLOW;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.net.http.HttpResponse;
import static java.awt.Color.CYAN;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.time.ZoneId;
import java.net.URI;

@Slf4j
@Component
public record GetJoke(
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
     * @param context the command context containing the information about the command.
     */
    private void process(CommandContext context){
        var url = jokesService.createJokeRequestUrl(
                context.getEvent().getOption("category"),
                context.getEvent().getOption("language")
        );
        log.info("Get Joke url: {}", url);
        final var REQUEST = httpConfig.httpBuilder()
                .uri(URI.create(url))
                .GET()
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

            var apiResponse = RESPONSE.body();

            String joke;

            try {
                joke = new ObjectMapper().readTree(apiResponse).get("joke").asText();
            } catch (JsonProcessingException e) {
                log.error("Error while parsing joke api response", e);
                throw new RuntimeException(e);
            }

            messageUtilService.getEmbedBuilder()
                    .clear()
                    .clearFields()
                    .setColor(CYAN)
                    .setTitle("GetJoke of the day")
                    .setDescription(joke != null ? joke : "No joke found")
                    .setTimestamp(now(ZoneId.of("UTC")))
                    .setFooter(
                            format("Created by %s", context.getGuild().getJDA().getSelfUser().getAsTag()),
                            context.getGuild().getJDA().getSelfUser().getAvatarUrl()
                    );
            context.getEvent()
                    .getInteraction()
                    .replyEmbeds(messageUtilService.getEmbedBuilder().build())
                    .queue();
        } else {
            messageUtilService.getEmbedBuilder()
                    .clear()
                    .clearFields()
                    .setColor(YELLOW)
                    .setTitle("No joke found")
                    .setDescription("I couldn't find a joke for you 😢.")
                    .setTimestamp(now(ZoneId.of("UTC")))
                    .setFooter(
                            format("Created by %s", context.getGuild().getJDA().getSelfUser().getAsTag()),
                            context.getGuild().getJDA().getSelfUser().getAvatarUrl()
                    );
            context.getEvent()
                    .getInteraction()
                    .replyEmbeds(messageUtilService.getEmbedBuilder().build())
                    .queue();
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
        return () -> "joke";
    }

    /**
     * Gets the command data.
     *
     * @return a {@code Supplier<CommandData>}.
     */
    @Override
    public Supplier<CommandData> getCommandData() {
        return () -> new CommandDataImpl(
                name().get(),
                description().get())
                .addOptions(
                        new OptionData(OptionType.STRING, "category", "Category of the joke", false)
                                .setDescription("Select your desired joke category")
                                .addChoices(jokesService.getCategories()),
                        new OptionData(OptionType.STRING, "language", "Language of the joke", false)
                                .setDescription("Select your desired joke language")
                                .addChoices(jokesService.getLanguages())
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
        return () -> "Get a random GetJoke";
    }
}
