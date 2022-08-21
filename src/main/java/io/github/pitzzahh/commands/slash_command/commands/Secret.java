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
import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Secret implements SlashCommand {

    private final MessageBuilder MESSAGE_BUILDER = new MessageBuilder();
    private final EmbedBuilder EMBED_BUILDER = new EmbedBuilder();

    @Override
    public Consumer<CommandContext> execute() {
        return context -> {
            if (context.getEvent().getChannel().getName().equals(Bot.getConfig.get().get("SECRET_CHANNEL"))) {

                final var CONFESSIONS = Bot.getShardManager.get()
                        .getTextChannelsByName(Bot.getConfig.get().get("SECRETS_CHANNEL"), false)
                        .stream()
                        .findAny();

                final var message = Objects.requireNonNull(context.getEvent().getOption("secret-message")).getAsString();

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
                                            .setDescription("Secret message sent");
                                    MESSAGE_BUILDER.clear()
                                            .setEmbeds(EMBED_BUILDER.build());
                                    context.getEvent()
                                            .getInteraction()
                                            .reply(MESSAGE_BUILDER.build())
                                            .setEphemeral(true)
                                            .queue();
                                })
                );
            }
        };
    }

    @Override
    public Supplier<String> name() {
        return () -> "secret";
    }

    @Override
    public Supplier<CommandData> getInfo() {
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
