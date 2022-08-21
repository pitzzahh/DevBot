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

import io.github.pitzzahh.Bot;
import io.github.pitzzahh.commands.slash_command.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import io.github.pitzzahh.commands.slash_command.SlashCommand;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Confess implements SlashCommand {

    private final MessageBuilder MESSAGE_BUILDER = new MessageBuilder();
    private final EmbedBuilder EMBED_BUILDER = new EmbedBuilder();

    @Override
    public void execute(@NotNull CommandContext context) {
        if (context.getEvent().getChannel().getName().equals(Bot.getConfig().get("CONFESSION_CHANNEL"))) {

            final var CONFESSIONS = Bot.getShardManager()
                    .getTextChannelsByName(Bot.getConfig().get("CONFESSIONS_CHANNEL"), false)
                    .stream()
                    .findAny();

            final var message = Objects.requireNonNull(context.getEvent().getOption("confession")).getAsString();

            EMBED_BUILDER.clear()
                    .clearFields()
                    .setColor(Color.RED)
                    .setDescription(message)
                    .setFooter("anonymous 👀")
                    .setTimestamp(LocalDateTime.now(ZoneId.of("UTC")));
            CONFESSIONS.ifPresent(
                    c -> c.sendMessageEmbeds(EMBED_BUILDER.build())
                            .queue(e -> {
                                EMBED_BUILDER.clear()
                                        .setColor(Color.GREEN)
                                        .setDescription("Confession sent")
                                        .setTimestamp(LocalDateTime.now(ZoneId.of("UTC")).plusSeconds(10))
                                        .setFooter("This message will be automatically deleted");
                                MESSAGE_BUILDER.clear()
                                                .setEmbeds(EMBED_BUILDER.build());
                                context.getEvent()
                                        .getInteraction()
                                        .reply(MESSAGE_BUILDER.build())
                                        .setEphemeral(true).queue(
                                        f -> f.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)
                                );
                            })
            );
        }
    }

    @Override
    public String name() {
        return "confess";
    }

    @Override
    public String description() {
        return "Confess something";
    }

    @Override
    public CommandData getInfo() {
        return new CommandDataImpl(
                name(),
                description()
        ).addOptions(
                new OptionData(
                        OptionType.STRING,
                        "confession",
                        "Enter your confession",
                        true)
        );
    }
}
