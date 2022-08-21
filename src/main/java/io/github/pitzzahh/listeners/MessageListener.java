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

import io.github.pitzzahh.moderation.MessageChecker;
import io.github.pitzzahh.utilities.Util;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.MessageBuilder;
import io.github.pitzzahh.commands.chat_command.CommandManager;
import org.jetbrains.annotations.NotNull;
import net.dv8tion.jda.api.EmbedBuilder;
import java.time.LocalDateTime;
import io.github.pitzzahh.Bot;
import java.time.ZoneId;
import java.awt.*;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MessageListener extends ListenerAdapter {

    private final CommandManager MANAGER = new CommandManager();
    private final MessageBuilder MESSAGE_BUILDER = new MessageBuilder();
    private final EmbedBuilder EMBED_BUILDER = new EmbedBuilder();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        final var AUTHOR = event.getAuthor();
        final var PREFIX = Bot.getConfig.get().get("PREFIX");
        final var MESSAGE = event.getMessage().getContentRaw();
        if (MESSAGE.startsWith(PREFIX)) MANAGER.handle(event);
        else {
            if (MESSAGE.equals(Bot.getConfig.get().get("VERIFY_MESSAGE"))) {
                final var IS_IN_VERIFY_CHANNEL = event.getChannel()
                        .getName()
                        .equals(Bot.getConfig.get().get("VERIFY_CHANNEL"));
                if (IS_IN_VERIFY_CHANNEL) {
                    final var BUTTON = Button.primary("verify-button", "Verify");
                    EMBED_BUILDER.clear()
                            .clearFields()
                            .setColor(Color.BLUE)
                            .setTitle("Verify yourself")
                            .appendDescription("Click the verify button to verify")
                            .setTimestamp(LocalDateTime.now(ZoneId.of("UTC")))
                            .setFooter(
                                    String.format("Created by %s", event.getAuthor().getAsTag()),
                                    event.getGuild().getIconUrl()
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
                                            String.format("Created by %s", event.getAuthor().getName()),
                                            event.getGuild().getIconUrl()
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
                            .queue(e -> {
                                        event.getMessage().delete().queue();
                                        e.delete().queueAfter(10, TimeUnit.SECONDS);
                            });
                }
                else {
                    var contains = MessageChecker.search(event.getMessage().getContentRaw());
                    System.out.println("is bad word = " + contains);
                    if (contains && !AUTHOR.isBot()) {
                        if (Util.violatedThreeTimes(AUTHOR.getName())) {
                            EMBED_BUILDER.clear()
                                    .clearFields()
                                    .setColor(Color.RED)
                                    .setTitle("Violated Three Times")
                                    .appendDescription(AUTHOR.getAsMention().concat("Cannot send messages for 5 minutes"))
                                    .setTimestamp(LocalDateTime.now(ZoneId.of("UTC")).plusSeconds(10))
                                    .setFooter(
                                            String.format("Scanned by %s", event.getAuthor().getName()),
                                            event.getGuild().getIconUrl()
                                    );
                            event.getChannel()
                                    .sendMessageEmbeds(EMBED_BUILDER.build())
                                    .queue();
                            AUTHOR.retrieveProfile()
                                    .timeout(5, TimeUnit.MINUTES)
                                    .queue();
                        }
                        else {
                            EMBED_BUILDER.clear()
                                    .clearFields()
                                    .setColor(Color.RED)
                                    .setTitle("Bad Word Detected")
                                    .appendDescription("Your message will be deleted 10 seconds from now")
                                    .setTimestamp(LocalDateTime.now(ZoneId.of("UTC")).plusSeconds(10))
                                    .setFooter(
                                            String.format("Scanned by %s", event.getAuthor().getName()),
                                            event.getGuild().getIconUrl()
                                    );
                            event.getMessage()
                                    .replyEmbeds(EMBED_BUILDER.build())
                                    .mentionRepliedUser(true)
                                    .queue();
                            Util.addViolation(AUTHOR.getName());
                            event.getMessage().delete().queueAfter(10, TimeUnit.SECONDS);
                        }
                    }
                }
            }
        }
    }
}
