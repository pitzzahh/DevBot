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

package tech.araopj.springpitzzahhbot.commands.slash_command.commands.joke.approveJoke;

import tech.araopj.springpitzzahhbot.commands.slash_command.commands.joke.service.JokesService;
import tech.araopj.springpitzzahhbot.commands.slash_command.CommandContext;
import tech.araopj.springpitzzahhbot.utilities.service.MessageUtilService;
import tech.araopj.springpitzzahhbot.commands.slash_command.SlashCommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;
import net.dv8tion.jda.api.Permission;
import java.util.concurrent.TimeUnit;
import static java.awt.Color.YELLOW;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public record ApproveJoke(
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

    /**
     * Contains the process to be executed.
     *
     * @param context the command context containing the information about the command.
     */
    private void process(CommandContext context) {
        log.info("Processing command: {}", name().get());
        var isAdmin = context.getMember()
                .getRoles()
                .stream()
                .anyMatch(r -> r.hasPermission(Permission.ADMINISTRATOR) ||
                               r.hasPermission(Permission.MANAGE_SERVER) ||
                               context.getMember().isOwner()
                );
        log.info("Is user {} an admin? and can manage this server?: {}", context.getMember().getAsMention(), isAdmin);
        if (isAdmin) {

            OptionMapping idOption = context.getEvent().getOption("joke-id");
            if (idOption != null) {
                log.info("Joke id: {}", idOption.getAsString());
                boolean noJokeWithId = jokesService.getSubmittedJokes()
                        .stream()
                        .noneMatch(j -> j.id() == Integer.parseInt(idOption.getAsString()));
                if (noJokeWithId) {
                    log.info("No joke with id: {}", idOption.getAsString());
                    messageUtilService.generateAutoDeleteMessage(
                            context.event(),
                            YELLOW,
                            "Testing",
                            String.format("No joke with id %s", idOption.getAsString())
                    );
                } else {
                    log.info("Joke with id: {} found", idOption.getAsString());
                    jokesService
                            .getSubmittedJokes()
                            .forEach(j -> {
                                if (j.id() == Integer.parseInt(idOption.getAsString())) {
                                    log.info(String.format("Joke with id %s has been approved", idOption.getAsString()));
                                    boolean isApproved = jokesService.approveJoke(j);
                                    if (isApproved) {
                                        messageUtilService.generateAutoDeleteMessage(
                                                context.event(),
                                                YELLOW,
                                                "Testing",
                                                String.format("Joke with id %s has been approved", idOption.getAsString())
                                        );
                                    } else {
                                        log.info(String.format("Joke with id %s has not been approved", idOption.getAsString()));
                                        messageUtilService.generateAutoDeleteMessage(
                                                context.event(),
                                                YELLOW,
                                                "Testing",
                                                String.format("Joke with id %s has not been approved", idOption.getAsString())
                                        );
                                    }
                                }
                            });
                }
            }

        }
        messageUtilService.generateAutoDeleteMessage(
                context.event(),
                YELLOW,
                "Testing",
                String.format("Is user %s an admin? and can manage this server?: %s", context.getMember().getAsMention(), isAdmin)
        );
        context.getEvent()
                .getInteraction()
                .replyEmbeds(messageUtilService.getEmbedBuilder().build())
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
        return () -> "approve-joke";
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
                        new OptionData(OptionType.STRING, "joke-id", "List of requested jokes", true)
                                .setDescription("Enter the id of the joke you want to approve")
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
        return () -> "Approves joke requests";
    }
}
