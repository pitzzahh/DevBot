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
package io.github.pitzzahh.listeners;

import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import io.github.pitzzahh.commands.chat_command.CommandManager;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import io.github.pitzzahh.moderation.MessageChecker;
import static io.github.pitzzahh.utilities.Util.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.time.format.DateTimeFormatter;
import io.github.pitzzahh.utilities.Util;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.TimeUnit;
import java.time.format.FormatStyle;
import java.time.LocalDateTime;
import io.github.pitzzahh.Bot;
import java.util.Objects;
import java.time.ZoneId;
import java.time.Clock;
import java.awt.*;

public class MessageListener extends ListenerAdapter {

    private final CommandManager MANAGER = new CommandManager();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        final var AUTHOR = event.getAuthor();
        final var PREFIX = Bot.getConfig.get().get("PREFIX");
        final var MESSAGE = event.getMessage().getContentRaw();
        if (MESSAGE.startsWith(PREFIX)) MANAGER.handle(event);
        else {
            if (MESSAGE.equals(Bot.getConfig.get().get("VERIFY_MESSAGE_COMMAND"))) {
                final var IS_IN_VERIFY_CHANNEL = event.getChannel()
                        .getName()
                        .equals(Bot.getConfig.get().get("VERIFY_CHANNEL_NAME"));
                if (IS_IN_VERIFY_CHANNEL) {
                    final var BUTTON = Button.primary("verify-button", "Verify");
                    EMBED_BUILDER.clear()
                            .clearFields()
                            .setColor(Color.BLUE)
                            .setTitle("Verify yourself")
                            .appendDescription("Click the verify button to verify")
                            .setTimestamp(LocalDateTime.now(ZoneId.of("UTC")))
                            .setFooter(
                                    String.format("Created by %s", event.getJDA().getSelfUser().getAsTag()),
                                    event.getJDA().getSelfUser().getAvatarUrl()
                            );
                    MESSAGE_BUILDER.clear()
                            .setActionRows(ActionRow.of(BUTTON))
                            .setEmbeds(EMBED_BUILDER.build());
                    event.getChannel()
                            .sendMessage(MESSAGE_BUILDER.build())
                            .queue(e -> {
                                if (!event.getMessage().isEdited()) event.getMessage().delete().queue();
                            });
                }
            }
            else if (MESSAGE.equals(Bot.getConfig.get().get("CREATE_SECRET_CATEGORY")) && Objects.requireNonNull(event.getMember()).isOwner()){
                final var CATEGORY_NAME = Bot.getConfig.get().get("CREATE_SECRET_CATEGORY");
                event.getGuild().createCategory(CATEGORY_NAME.replace(CATEGORY_NAME.charAt(0), ' ')).queue(
                        category -> {
                            EMBED_BUILDER.clear()
                                    .clearFields()
                                    .setColor(Color.CYAN)
                                    .setTitle("Write your secret here")
                                    .setDescription("your secret will be anonymous")
                                    .appendDescription(", use `/secret` to tell a secret")
                                    .setFooter(
                                            String.format("Created by %s", event.getJDA().getSelfUser().getAsTag()),
                                            category.getJDA().getSelfUser().getAvatarUrl()
                                    );
                            category.createTextChannel(Bot.getConfig.get().get("SECRET_CHANNEL"))
                                    .queue(c -> c.sendMessageEmbeds(EMBED_BUILDER.build()).queue());
                            category.createTextChannel(Bot.getConfig.get().get("SECRETS_CHANNEL"))
                                    .queue();
                        }
                );
            } else {
                if (event.getChannel().getName().equals(Bot.getConfig.get().get("SECRET_CHANNEL")) && !event.getAuthor().isBot()) {
                    EMBED_BUILDER.clear()
                            .clearFields()
                            .setColor(Color.RED)
                            .appendDescription("Please use `/secret` to tell a secret")
                            .setTimestamp(LocalDateTime.now(ZoneId.of("UTC")).plusSeconds(10))
                            .setFooter("This message will be automatically deleted");
                    event.getMessage()
                            .replyEmbeds(EMBED_BUILDER.build())
                            .queue(e -> e.delete().queueAfter(5, TimeUnit.SECONDS));
                    event.getMessage().delete().queue();
                }
                else if (!AUTHOR.isBot()){
                    var contains = MessageChecker.search.apply(event.getMessage().getContentRaw());
                    System.out.println("is bad word = " + contains);
                    if (contains && !AUTHOR.isBot()) {
                        Util.addViolation(AUTHOR.getName());
                        var isVeryBad = Util.violatedThreeTimes(AUTHOR.getName());
                        if (isVeryBad) {
                            EMBED_BUILDER.clear()
                                    .clearFields()
                                    .setColor(Color.RED)
                                    .setTitle("Violated Three Times")
                                    .appendDescription(
                                            String.format(
                                                    AUTHOR.getAsMention().concat(" Cannot send messages until %s"),
                                                    LocalDateTime.now(Clock.systemDefaultZone())
                                                            .plusMinutes(1)
                                                            .format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
                                            )
                                    )
                                    .setFooter(
                                            String.format("Scanned by %s", event.getJDA().getSelfUser().getAsTag()),
                                            event.getJDA().getSelfUser().getAvatarUrl()
                                    );
                            event.getChannel()
                                    .sendMessageEmbeds(EMBED_BUILDER.build())
                                    .queue();
                            AUTHOR.retrieveProfile()
                                    .timeout(5, TimeUnit.MINUTES)
                                    .queue();
                            event.getMessage().delete().queueAfter(2, TimeUnit.SECONDS);
                        }
                        else {
                            EMBED_BUILDER.clear()
                                    .clearFields()
                                    .setColor(Color.RED)
                                    .setTitle("Bad Word Detected")
                                    .appendDescription(
                                            String.format(
                                                    "This message will be deleted on %s",
                                                    LocalDateTime.now(Clock.systemDefaultZone())
                                                            .plusMinutes(1)
                                                            .format(
                                                                    DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
                                                            )
                                            )
                                    )
                                    .setFooter(
                                            String.format("Scanned by %s", event.getJDA().getSelfUser().getAsTag()),
                                            event.getJDA().getSelfUser().getAvatarUrl()
                                    );
                            event.getMessage()
                                    .replyEmbeds(EMBED_BUILDER.build())
                                    .mentionRepliedUser(true)
                                    .queue();
                            event.getMessage().delete().queueAfter(5, TimeUnit.SECONDS);
                        }
                    }

                    if (isTheOneWhoPlays(AUTHOR.getName())) {
                        var isCorrect = answer(AUTHOR.getName(), MESSAGE);
                        if (isCorrect) {
                            EMBED_BUILDER.clear()
                                    .clearFields()
                                    .setColor(Color.BLUE)
                                    .setTitle("Correct!");
                            event.getMessage()
                                    .replyEmbeds(EMBED_BUILDER.build())
                                    .queue();
                        }
                        else {
                            if (isTheOneWhoPlays(AUTHOR.getName())) {
                                EMBED_BUILDER.clear()
                                        .clearFields()
                                        .setColor(Color.RED)
                                        .setTitle("WRONG ANSWER");
                                event.getMessage()
                                        .replyEmbeds(EMBED_BUILDER.build())
                                        .queue();
                            }
                        }
                    }
                }
            }
        }
    }
}
