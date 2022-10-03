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

import static io.github.pitzzahh.utilities.Channels.getTextChannel;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import static java.time.format.DateTimeFormatter.ofLocalizedTime;
import io.github.pitzzahh.commands.slash_command.CommandContext;
import static io.github.pitzzahh.utilities.Util.MESSAGE_BUILDER;
import io.github.pitzzahh.commands.slash_command.SlashCommand;
import static io.github.pitzzahh.utilities.Util.EMBED_BUILDER;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import static io.github.pitzzahh.utilities.IChannels.*;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.time.format.FormatStyle.SHORT;
import net.dv8tion.jda.api.entities.Message;
import static java.time.LocalDateTime.now;
import static java.lang.String.format;
import java.util.function.Consumer;
import java.util.function.Supplier;
import static java.awt.Color.GREEN;
import static java.time.ZoneId.of;
import io.github.pitzzahh.Bot;
import java.util.Objects;
import java.awt.*;

/**
 * Class used to manage secret slash command.
 */
public class Secret implements SlashCommand {

    @Override
    public Consumer<CommandContext> execute() {
        return this::process;
    }

    /**
     * Contains the process to be executed.
     * @param context the command context containing the information about the command.
     */
    private void process(CommandContext context) {
        if (context.getEvent().getChannel().getName().equals(ENTER_SECRET_CHANNEL)) {

            final var CONFESSIONS = Bot.getShardManager.get()
                    .getTextChannelsByName(SENT_SECRET_CHANNEL, false)
                    .stream()
                    .findAny();

            final var SECRET_MESSAGE = Objects.requireNonNull(context.getEvent().getOption("secret-message")).getAsString();

            EMBED_BUILDER
                    .clear()
                    .clearFields()
                    .setColor(Color.RED)
                    .setDescription(SECRET_MESSAGE)
                    .setFooter("anonymous 👀")
                    .setTimestamp(now(of("UTC")));
            CONFESSIONS.ifPresent(
                    c -> c.sendMessageEmbeds(EMBED_BUILDER.build())
                            .queue(message -> confirmationMessage(context, message))
            );
        }
        else {
            EMBED_BUILDER.clear()
                    .setColor(GREEN)
                    .setTitle("Cannot use command here")
                    .setDescription(format("To tell a secret message, go to %s", getTextChannel(ENTER_SECRET_CHANNEL).getAsMention()))
                    .setFooter(format("This message will be deleted on %s",
                                    now(of("UTC"))
                                            .plusMinutes(1)
                                            .format(ofLocalizedTime(SHORT))
                            )
                    );

            MESSAGE_BUILDER
                    .clear()
                    .setEmbeds(EMBED_BUILDER.build());
            context.getEvent()
                    .getInteraction()
                    .reply(MESSAGE_BUILDER.build())
                    .setEphemeral(true)
                    .queue();
        }
    }
    private void confirmationMessage(CommandContext context, Message message) {
        EMBED_BUILDER
                .clear()
                .setColor(GREEN)
                .setTitle("Secret message sent")
                .setDescription("This message will be deleted")
                .setFooter(
                        format("This message will be deleted on %s",
                                now(of("UTC"))
                                        .plusMinutes(1)
                                        .format(
                                                ofLocalizedTime(SHORT)
                                        )
                        )
                );

        MESSAGE_BUILDER
                .clear()
                .setEmbeds(EMBED_BUILDER.build());
        context.getEvent()
                .getInteraction()
                .reply(MESSAGE_BUILDER.build())
                .setEphemeral(true)
                .queue();
        message.delete().queueAfter(1, MINUTES);
    }

    @Override
    public Supplier<String> name() {
        return () -> "secret";
    }

    @Override
    public Supplier<CommandData> getCommandData() {
        return () -> new CommandDataImpl(
                name().get(),
                description().get()
        ).addOptions(
                new OptionData(
                        OptionType.STRING,
                        "secret-message",
                        "Enter your secret message",
                        true)
        );
    }

    @Override
    public Supplier<String> description() {
        return () -> "Tell a secret message";
    }
}
