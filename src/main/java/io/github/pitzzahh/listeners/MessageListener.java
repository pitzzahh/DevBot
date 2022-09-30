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

import static net.dv8tion.jda.api.interactions.components.buttons.Button.primary;
import static io.github.pitzzahh.utilities.validation.Validator.isDecimalNumber;
import static io.github.pitzzahh.utilities.validation.Validator.isWholeNumber;
import static net.dv8tion.jda.api.interactions.components.ActionRow.of;
import static io.github.pitzzahh.moderation.MessageChecker.search;
import static java.time.format.DateTimeFormatter.ofLocalizedTime;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import io.github.pitzzahh.commands.chat_command.CommandManager;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static io.github.pitzzahh.utilities.Util.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import static java.time.format.FormatStyle.SHORT;
import static java.time.Clock.systemDefaultZone;
import static io.github.pitzzahh.Bot.getConfig;
import static java.time.LocalDateTime.now;
import io.github.pitzzahh.utilities.Util;
import org.jetbrains.annotations.NotNull;
import static java.lang.String.format;
import static java.time.ZoneId.of;
import static java.awt.Color.*;
import java.util.Objects;

/**
 * Class that listens to messages on text channels.
 */
public class MessageListener extends ListenerAdapter {

    private final CommandManager MANAGER = new CommandManager();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        final var AUTHOR = event.getAuthor();
        final var PREFIX = getConfig.get().get("PREFIX");
        final var MESSAGE = event.getMessage().getContentRaw();
        if (MESSAGE.startsWith(PREFIX)) MANAGER.handle(event);
        else {
            if (MESSAGE.equals(getConfig.get().get("VERIFY_MESSAGE_COMMAND"))) {
                final var IS_IN_VERIFY_CHANNEL = event.getChannel()
                        .getName()
                        .equals(getConfig.get().get("VERIFY_CHANNEL_NAME"));
                if (IS_IN_VERIFY_CHANNEL) {
                    final var BUTTON = primary("verify-button", "Verify");
                    EMBED_BUILDER.clear()
                            .clearFields()
                            .setColor(BLUE)
                            .setTitle("Verify yourself")
                            .appendDescription("Click the verify button to verify")
                            .setTimestamp(now(of("UTC")))
                            .setFooter(
                                    format("Created by %s", event.getJDA().getSelfUser().getAsTag()),
                                    event.getJDA().getSelfUser().getAvatarUrl()
                            );
                    MESSAGE_BUILDER.clear()
                            .setActionRows(of(BUTTON))
                            .setEmbeds(EMBED_BUILDER.build());
                    event.getChannel()
                            .sendMessage(MESSAGE_BUILDER.build())
                            .queue(e -> {
                                if (!event.getMessage().isEdited()) event.getMessage().delete().queue();
                            });
                }
            }
            else if (MESSAGE.equals(getConfig.get().get("CREATE_SECRET_CATEGORY")) && Objects.requireNonNull(event.getMember()).isOwner()){
                final var CATEGORY_NAME = getConfig.get().get("CREATE_SECRET_CATEGORY");
                event.getGuild().createCategory(CATEGORY_NAME.replace(CATEGORY_NAME.charAt(0), ' ')).queue(
                        category -> {
                            EMBED_BUILDER.clear()
                                    .clearFields()
                                    .setColor(CYAN)
                                    .setTitle("Write your secret here")
                                    .setDescription("your secret will be anonymous")
                                    .appendDescription(", use `/secret` to tell a secret")
                                    .setFooter(
                                            format("Created by %s", event.getJDA().getSelfUser().getAsTag()),
                                            category.getJDA().getSelfUser().getAvatarUrl()
                                    );
                            category.createTextChannel(getConfig.get().get("SECRET_CHANNEL"))
                                    .queue(c -> c.sendMessageEmbeds(EMBED_BUILDER.build()).queue());
                            category.createTextChannel(getConfig.get().get("SECRETS_CHANNEL"))
                                    .queue();
                        }
                );
            } else {
                if (event.getChannel().getName().equals(getConfig.get().get("SECRET_CHANNEL")) && !event.getAuthor().isBot()) {
                    EMBED_BUILDER.clear()
                            .clearFields()
                            .setColor(RED)
                            .appendDescription("Please use `/secret` to tell a secret")
                            .setTimestamp(now(of("UTC")).plusSeconds(10))
                            .setFooter("This message will be automatically deleted");
                    event.getMessage()
                            .replyEmbeds(EMBED_BUILDER.build())
                            .queue(e -> e.delete().queueAfter(5, SECONDS));
                    event.getMessage().delete().queue();
                }
                else if (!AUTHOR.isBot()){
                    var contains = search.apply(event.getMessage().getContentRaw());
                    System.out.println("is bad word = " + contains);
                    if (contains && !AUTHOR.isBot()) {
                        Util.addViolation(AUTHOR.getName());
                        var isVeryBad = violatedThreeTimes(AUTHOR.getName());
                        if (isVeryBad) {
                            EMBED_BUILDER.clear()
                                    .clearFields()
                                    .setColor(RED)
                                    .setTitle("Violated Three Times")
                                    .appendDescription(
                                            format(
                                                    AUTHOR.getAsMention().concat(" Cannot send messages until %s"),
                                                    now(systemDefaultZone())
                                                            .plusMinutes(1)
                                                            .format(ofLocalizedTime(SHORT))
                                            )
                                    )
                                    .setFooter(
                                            format("Scanned by %s", event.getJDA().getSelfUser().getAsTag()),
                                            event.getJDA().getSelfUser().getAvatarUrl()
                                    );
                            event.getChannel()
                                    .sendMessageEmbeds(EMBED_BUILDER.build())
                                    .queue();
                            AUTHOR.retrieveProfile()
                                    .timeout(5, MINUTES)
                                    .queue();
                            event.getMessage().delete().queueAfter(2, SECONDS);
                        }
                        else {
                            EMBED_BUILDER.clear()
                                    .clearFields()
                                    .setColor(RED)
                                    .setTitle("Bad Word Detected")
                                    .appendDescription(
                                            format(
                                                    "This message will be deleted on %s",
                                                    now(systemDefaultZone())
                                                            .plusMinutes(1)
                                                            .format(
                                                                    ofLocalizedTime(SHORT)
                                                            )
                                            )
                                    )
                                    .setFooter(
                                            format("Scanned by %s", event.getJDA().getSelfUser().getAsTag()),
                                            event.getJDA().getSelfUser().getAvatarUrl()
                                    );
                            event.getMessage()
                                    .replyEmbeds(EMBED_BUILDER.build())
                                    .mentionRepliedUser(true)
                                    .queue();
                            event.getMessage().delete().queueAfter(5, SECONDS);
                        }
                    }

                    if (isTheOneWhoPlays(AUTHOR.getName())) {
                        final var IS_CORRECT = answer(AUTHOR.getName(), MESSAGE);
                        if (isWholeNumber().or(isDecimalNumber()).test(MESSAGE)) {
                            if (IS_CORRECT) {
                                EMBED_BUILDER.clear()
                                        .clearFields()
                                        .setColor(BLUE)
                                        .setTitle("Correct!");
                                event.getMessage()
                                        .replyEmbeds(EMBED_BUILDER.build())
                                        .queue();
                            }
                            else {
                                EMBED_BUILDER.clear()
                                        .clearFields()
                                        .setColor(RED)
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
