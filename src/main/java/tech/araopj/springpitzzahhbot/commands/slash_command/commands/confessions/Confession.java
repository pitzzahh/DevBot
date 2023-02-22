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

package tech.araopj.springpitzzahhbot.commands.slash_command.commands.confessions;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.springframework.stereotype.Component;
import tech.araopj.springpitzzahhbot.commands.service.CommandsService;
import tech.araopj.springpitzzahhbot.commands.slash_command.CommandContext;
import tech.araopj.springpitzzahhbot.commands.slash_command.SlashCommand;
import tech.araopj.springpitzzahhbot.commands.slash_command.commands.confessions.service.ConfessionService;
import tech.araopj.springpitzzahhbot.config.channels.service.ChannelService;
import tech.araopj.springpitzzahhbot.utilities.MessageUtil;
import java.awt.*;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import static java.awt.Color.GREEN;
import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static java.time.ZoneId.of;
import static java.time.format.DateTimeFormatter.ofLocalizedTime;
import static java.time.format.FormatStyle.SHORT;
import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * Class used to manage confessions slash command.
 */
@Slf4j
@Component
public record Confession(
        ConfessionService confessionService,
        CommandsService commandsService,
        ChannelService channelService,
        MessageUtil messageUtil
) implements SlashCommand {

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
        if (context.getEvent().getChannel().getName().equals(confessionService.enterSecretChannelName())) {
            log.info("User {} used confession command in {} channel", context.getEvent().getUser().getAsTag(), context.getEvent().getChannel().getName());
            final var CONFESSIONS = channelService.getChannelByName(context.getEvent(), confessionService.sentSecretChannelName());

            final var SECRET_MESSAGE = Objects.requireNonNull(
                    context.getEvent().getOption(name().get().concat("ion"))
            ).getAsString();

            messageUtil.getEmbedBuilder()
                    .clear()
                    .clearFields()
                    .setColor(Color.RED)
                    .setDescription(SECRET_MESSAGE)
                    .setFooter("anonymous 👀")
                    .setTimestamp(now(of("UTC")));
            CONFESSIONS.ifPresent(c -> c.sendMessageEmbeds(messageUtil.getEmbedBuilder().build()).queue(message -> confirmationMessage(context)));
            log.info("Sent confession message to {} channel", confessionService.sentSecretChannelName());
        } else {
            log.warn("User {} tried to use confession command in {} channel", context.getEvent().getUser().getAsTag(), context.getEvent().getChannel().getName());
            message(
                    "Cannot use command here",
                    format(
                            "To tell a confessions, go to %s",
                            channelService
                                    .getChannelByName(
                                            context.event(),
                                            confessionService.enterSecretChannelName()
                                    )
                                    .map(TextChannel::getAsMention)
                                    .orElse("channel")
                    )
            );
            context.getEvent()
                    .getInteraction()
                    .reply(messageUtil.getMessageBuilder().build())
                    .setEphemeral(true)
                    .queue();
            log.info("Sent ephemeral message to user {}", context.getEvent().getUser().getAsTag());
        }
    }

    /**
     * Constructs an embedded message.
     *
     * @param title       the title of the message.
     * @param description the description of the message.
     */
    private void message(String title, String description) {
        messageUtil.getEmbedBuilder().clear()
                .setColor(GREEN)
                .setTitle(title)
                .setDescription(description)
                .setFooter(
                        format(
                                "This message will be deleted on %s",
                                now().plusMinutes(commandsService.messageDeletionDelay())
                                        .format(ofLocalizedTime(SHORT))
                        )
                );
        messageUtil.getMessageBuilder()
                .clear()
                .setEmbeds(messageUtil.getEmbedBuilder().build());
    }

    /**
     * Constructs the confirmation message when a user send a confessions message.
     *
     * @param context the context of the command.
     */
    private void confirmationMessage(CommandContext context) {
        message(
                "Confession message sent",
                format(
                        "Your secret message has been sent to the %s channel",
                        channelService
                                .getChannelByName(
                                        context.event(),
                                        confessionService.sentSecretChannelName()
                                )
                                .map(TextChannel::getAsMention)
                                .orElse("channel")
                )
        );
        context.getEvent()
                .getInteraction()
                .reply(messageUtil.getMessageBuilder().build())
                .setEphemeral(true)
                .queue(m -> m.deleteOriginal().queueAfter(commandsService.messageDeletionDelay(), MINUTES));
    }

    /**
     * Supplies the name of the slash command.
     *
     * @return a {@code Supplier<String>}.
     * @see Supplier
     */
    @Override
    public Supplier<String> name() {
        return () -> "confess";
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
                description().get()
        ).addOptions(
                new OptionData(
                        OptionType.STRING,
                        name().get().concat("ion"),
                        "Enter your confession",
                        true)
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
        return () -> "Tell a confession message";
    }
}
