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
package tech.araopj.devbot.listeners;

import static io.github.pitzzahh.util.utilities.validation.Validator.isDecimalNumber;
import static io.github.pitzzahh.util.utilities.validation.Validator.isWholeNumber;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import tech.araopj.devbot.commands.chat_commands.ChatCommandManager;
import tech.araopj.devbot.services.CommandsService;
import tech.araopj.devbot.commands.slash_commands.commands.Confession;
import tech.araopj.devbot.services.RoleService;
import tech.araopj.devbot.services.slash_commands.ConfessionService;
import tech.araopj.devbot.services.configs.CategoryService;
import tech.araopj.devbot.services.configs.ChannelService;
import tech.araopj.devbot.services.slash_commands.GameService;
import tech.araopj.devbot.services.configs.MessageCheckerService;
import tech.araopj.devbot.services.configs.ViolationService;
import tech.araopj.devbot.services.MessageUtilService;

import java.time.ZoneId;
import java.util.EnumSet;
import java.util.Objects;

import static java.awt.Color.*;
import static java.lang.String.format;
import static java.time.LocalDateTime.now;

/**
 * Class that listens to messages on text channels.
 */
@Slf4j
@Component
@AllArgsConstructor
public class MessageListener extends ListenerAdapter { // TODO: Decouple code

    private final MessageCheckerService messageCheckerService;
    private final ChatCommandManager chatCommandManager;
    private final MessageUtilService messageUtilService;
    private final ConfessionService confessionService;
    private final ViolationService violationService;
    private final CommandsService commandsService;
    private final CategoryService categoryService;
    private final ChannelService channelService;
    private final GameService gameService;
    private final RoleService roleService;
    private final Confession confession;

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        final var AUTHOR = event.getAuthor();
        final var PREFIX = commandsService.getPrefix();
        final var MESSAGE = event.getMessage().getContentRaw();
        if (MESSAGE.startsWith(PREFIX)) {
            log.info("ChatCommand received: {}", MESSAGE);
            log.info("Commands started with: {}", PREFIX);
            chatCommandManager.handle(event);
        } else {
            final var adminRole = roleService.getRoleOrElseThrow(event.getGuild(), "Cannot find admin role", "Admin", true);

            if (MESSAGE.equals(commandsService.getRulesCommand()) && (Objects.requireNonNull(event.getMember()).isOwner() || event.getMember().getRoles().contains(adminRole))) {
                log.info("ChatCommand received: {}", MESSAGE);
                messageUtilService.generateRulesMessage(event);

                var rules = event.getGuild().getTextChannels()
                        .stream()
                        .anyMatch(tc -> tc.getName().equalsIgnoreCase("rules"));

                if (!rules) {
                    event.getGuild().createTextChannel("rules")
                            .setTopic("Rules and verification")
                            .queue(message -> sendVerificationMessage(event));
                }

                sendVerificationMessage(event);
            } else {
                var sentSecretChannel = confessionService.sentSecretChannelName();
                if (MESSAGE.equals(commandsService.getConfessCommand()) && (Objects.requireNonNull(event.getMember()).isOwner() || event.getMember().getRoles().contains(adminRole))) {
                    final var verifiedRole = roleService.getRoleOrElseThrow(event.getGuild(), "Cannot find verified role", "verified", false);
                    event.getGuild()
                            .createCategory(categoryService.secretsCategoryName())
                            .queue(
                                    category -> {
                                        messageUtilService.generateBotSentMessage(
                                                event,
                                                CYAN,
                                                "Write your confessions here",
                                                "your confessions will be anonymous".concat(format(", use `/%s` to tell a confession", confession.name().get())),
                                                now(ZoneId.of("UTC")),
                                                format("Created by %s", event.getJDA().getSelfUser().getAsTag())
                                        );
                                        category.createTextChannel(confessionService.enterSecretChannelName())
                                                .setTopic("This is a channel where you can write your confessions")
                                                .queue(c -> c.sendMessageEmbeds(messageUtilService.getEmbedBuilder().build()).queue());
                                        category.createTextChannel(sentSecretChannel)
                                                .addPermissionOverride(verifiedRole, null, EnumSet.of(Permission.MESSAGE_SEND))
                                                .setTopic("This is a channel contains all the confessions made by users")
                                                .queue();
                                    }
                            );
                } else {
                    if (event.getChannel().getName().equals(sentSecretChannel) && !event.getAuthor().isBot()) {
                        messageUtilService.getEmbedBuilder().clear()
                                .clearFields()
                                .setColor(RED)
                                .appendDescription(format("Please use `/%s` to tell a confessions", confession.name().get()))
                                .setTimestamp(now(ZoneId.of("UTC")).plusMinutes(messageUtilService.getReplyDeletionDelayInMinutes()))
                                .setFooter("This message will be automatically deleted on");
                        event.getMessage()
                                .replyEmbeds(messageUtilService.getEmbedBuilder().build())
                                .queue(e -> e.delete().queueAfter(messageUtilService.getReplyDeletionDelayInMinutes(), MINUTES));
                        event.getMessage().delete().queue();
                    }

                    // TODO: refactor embedded messages, remove code and effort duplication
                    else if (!AUTHOR.isBot()) {
                        var contains = messageCheckerService.searchForBadWord(event.getMessage().getContentRaw());
                        log.info("is bad word = " + contains);
                        if (contains && !AUTHOR.isBot()) {
                            violationService.addViolation(AUTHOR.getName());
                            var isVeryBad = violationService.violatedThreeTimes(AUTHOR.getName());
                            if (isVeryBad) {
                                messageUtilService.generateBotSentMessage(
                                        event,
                                        RED,
                                        "Violated Three Times",
                                        "Cannot send messages until " + now(ZoneId.of("UTC")).plusMinutes(5),
                                        now(ZoneId.of("UTC")),
                                        format("Scanned by %s", event.getJDA().getSelfUser().getAsTag())
                                );
                                event.getChannel()
                                        .sendMessageEmbeds(messageUtilService.getEmbedBuilder().build())
                                        .queue();
                                AUTHOR.retrieveProfile()
                                        .timeout(5, MINUTES) // TODO: use config to get the time out
                                        .queue();
                                event.getMessage().delete().queueAfter(messageUtilService.getMessageDeletionDelayInSeconds(), SECONDS);
                            } else {
                                messageUtilService.generateAutoDeleteMessage(event, RED, "Bad Word Detected", "Please don't use bad words");
                                event.getMessage()
                                        .replyEmbeds(messageUtilService.getEmbedBuilder().build())
                                        .mentionRepliedUser(true)
                                        .queue(m -> m.delete().queueAfter(messageUtilService.getReplyDeletionDelayInMinutes(), MINUTES));
                                event.getMessage().delete().queueAfter(messageUtilService.getMessageDeletionDelayInSeconds(), SECONDS);
                            }
                        }

                        if (gameService.isTheOneWhoPlays(AUTHOR.getName())) {
                            isWholeNumber().test(MESSAGE);
                            if (isWholeNumber().or(isDecimalNumber()).test(MESSAGE)) {
                                final var IS_CORRECT = gameService.processAnswer(MESSAGE);
                                if (IS_CORRECT) {
                                    messageUtilService.generateBotSentMessage(
                                            event,
                                            BLUE,
                                            "Correct!",
                                            "You got it right!",
                                            now(ZoneId.of("UTC")),
                                            format("Checked by %s", event.getJDA().getSelfUser().getAsTag())
                                    );
                                    event.getMessage()
                                            .replyEmbeds(messageUtilService.getEmbedBuilder().build())
                                            .queue();
                                } else {
                                    messageUtilService.generateBotSentMessage(
                                            event,
                                            BLUE,
                                            "Wrong answer!",
                                            "Correct answer is " + gameService.getAnswer(AUTHOR.getName()),
                                            now(ZoneId.of("UTC")),
                                            format("Checked by %s", event.getJDA().getSelfUser().getAsTag())
                                    );
                                    event.getMessage()
                                            .replyEmbeds(messageUtilService.getEmbedBuilder().build())
                                            .queue();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void sendVerificationMessage(@NotNull MessageReceivedEvent event) {
        messageUtilService.generateAutoDeleteMessage(
                event,
                BLUE,
                "Rules",
                format("Rules sent %s", channelService
                        .getChannelByName(event, "rules")
                        .map(TextChannel::getAsMention)
                        .orElse("general"))
        );
        event.getMessage()
                .replyEmbeds(messageUtilService.getEmbedBuilder().build())
                .queue(s -> s.delete().queueAfter(messageUtilService.getReplyDeletionDelayInMinutes(), MINUTES));
    }
}
