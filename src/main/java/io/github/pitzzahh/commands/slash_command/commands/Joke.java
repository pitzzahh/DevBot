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

package io.github.pitzzahh.commands.slash_command.commands;

import io.github.pitzzahh.commands.slash_command.CommandContext;
import io.github.pitzzahh.commands.slash_command.SlashCommand;
import static io.github.pitzzahh.utilities.Util.EMBED_BUILDER;
import static java.time.LocalDateTime.now;
import static java.lang.String.format;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.net.http.HttpResponse;
import static java.awt.Color.CYAN;
import static java.time.ZoneId.of;
import java.net.http.HttpRequest;
import java.net.http.HttpClient;
import java.io.IOException;
import java.net.URI;

public class Joke implements SlashCommand {

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
    private void process(CommandContext context) {
        final var CLIENT = HttpClient.newHttpClient();

        final var REQUEST = HttpRequest.newBuilder()
                .uri(URI.create("https://v2.jokeapi.dev/joke/Any?blacklistFlags=nsfw,religious,political,racist,sexist,explicit&format=txt"))
                .GET()
                .build();
        final HttpResponse<String> RESPONSE;

        try {
            RESPONSE = CLIENT.send(REQUEST, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        var joke = RESPONSE.body();
        EMBED_BUILDER.clear()
                .clearFields()
                .setColor(CYAN)
                .setTitle("Joke of the day")
                .setDescription(joke)
                .setTimestamp(now(of("UTC")))
                .setFooter(
                        format("Created by %s", context.getGuild().getJDA().getSelfUser().getAsTag()),
                        context.getGuild().getJDA().getSelfUser().getAvatarUrl()
                );
        if (RESPONSE.statusCode() == 200) {
            context.getEvent()
                    .getInteraction()
                    .replyEmbeds(EMBED_BUILDER.build())
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
     * Supplies the description of a slash command.
     *
     * @return a {code Supplier<String>} containing the description of the command.
     * @see Supplier
     */
    @Override
    public Supplier<String> description() {
        return () -> "Returns a random Dad Joke";
    }
}
