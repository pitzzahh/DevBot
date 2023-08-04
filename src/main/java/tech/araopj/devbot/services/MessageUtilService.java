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

package tech.araopj.devbot.services;

import tech.araopj.devbot.services.configs.ChannelService;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import tech.araopj.devbot.configs.MessageUtilConfig;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.entities.TextChannel;
import org.springframework.stereotype.Service;
import java.time.temporal.TemporalAccessor;
import static java.time.LocalDateTime.now;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.EmbedBuilder;
import static java.lang.String.format;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.awt.*;

@Slf4j
@Service
public record MessageUtilService(
        MessageUtilConfig messageUtilConfig,
        ChannelService channelService
) {

    public int getReplyDeletionDelayInMinutes() {
        return messageUtilConfig.getReplyDeletionDelayInMinutes();
    }

    public int getMessageDeletionDelayInSeconds() {
        return messageUtilConfig.getMessageDeletionDelayInSeconds();
    }

    public EmbedBuilder getEmbedBuilder() {
        return messageUtilConfig.getEmbedBuilder();
    }

    public MessageBuilder getMessageBuilder() {
        return messageUtilConfig.getMessageBuilder();
    }

    /**
     * This method is used to generate an embed message with color.
     *
     * @param color The color of the message.
     * @param title The title of the message.
     *              The default title is "Message".
     *              If the title is empty, the default title will be used.
     *              If the title is null, the title will be empty.
     */
    public void generateAutoDeleteMessage(Event event, Color color, String title, String description) {
        log.info("Message is being generated...");
        getEmbedBuilder()
                .clear()
                .clearFields()
                .setColor(color)
                .setTitle(title != null && !title.isEmpty() ? title : "Message")
                .setDescription(description != null && !description.isEmpty() ? description : "No description provided.")
                .setTimestamp(now(ZoneId.of("UTC")).plusMinutes(getReplyDeletionDelayInMinutes()))
                .setFooter("This message will be automatically deleted on", event.getJDA().getSelfUser().getAvatarUrl());
    }

    public void generateBotSentMessage(Event event, Color color, String title, String description, TemporalAccessor time, String footer) {
        log.info("Message is being generated...");
        getEmbedBuilder()
                .clear()
                .clearFields()
                .setColor(color)
                .setTitle(title != null && !title.isEmpty() ? title : "Message")
                .setDescription(description != null && !description.isEmpty() ? description : "No description provided.")
                .setTimestamp(time)
                .setFooter(
                        footer,
                        event.getJDA().getSelfUser().getAvatarUrl()
                );
    }

    public void generateRulesMessage(MessageReceivedEvent event) {
        log.info("Rules message is being generated...");
        final var verifyButton = Button.primary("verify-button", "Verify");
        getEmbedBuilder()
                .clear()
                .clearFields()
                .setColor(Color.BLUE)
                .setImage("https://images.unsplash.com/photo-1508726096737-5ac7ca26345f?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=812&q=80")
                .setTitle("Rules 📏")
                .setDescription("Please read the rules carefully before you start chatting.")
                .addField(
                        "Rule 1: Be respectful",
                        """
                                Treat others with kindness and respect. Do not engage in hate speech, harassment,\s
                                or bullying of any kind. This includes discriminatory language and slurs based on race,\s
                                ethnicity, gender, sexual orientation, or religion.
                                """,
                        false
                )
                .addBlankField(false)
                .addField(
                        "Rule 2: Keep conversations appropriate",
                        """
                                Keep conversations appropriate for all ages. Do not post content that is sexually explicit,\s
                                violent, or otherwise inappropriate for a general audience.
                                """,
                        false
                )
                .addBlankField(false)
                .addField(
                        "Rule 3: No spamming",
                        """
                                Do not spam or flood the chat with messages. This includes excessive emojis, text,\s
                                or images. Do not post the same message repeatedly.
                                """,
                        false
                )
                .addBlankField(false)
                .addField(
                        "Rule 4: No NSFW content",
                        """
                                Do not post NSFW content. This includes sexually explicit content, nudity, or gore.
                                """,
                        false
                )
                .addBlankField(false)
                .addField(
                        "Rule 5: No personal information",
                        """
                                Do not post personal information, such as phone numbers, addresses, or other identifying\s
                                information. Do not post other people's personal information without their permission.
                                """,
                        false
                )
                .addBlankField(false)
                .addField(
                        "Rule 6: No impersonation",
                        """
                                Do not impersonate other users or public figures. Do not create accounts with similar names\s
                                to other users.
                                """,
                        false
                )
                .addBlankField(false)
                .addField(
                        "Rule 7: No cheating",
                        """
                                Do not cheat or exploit bugs in the game. Do not use third-party software to gain an unfair\s
                                advantage.
                                """,
                        false
                )
                .addBlankField(false)
                .addField(
                        "Rule 8: No hacking",
                        """
                                Do not hack or attempt to hack the game or other users. Do not use third-party software to gain\s
                                an unfair advantage.
                                """,
                        false
                )
                .addBlankField(false)
                .addField(
                        "Rule 9: Follow Discord's community guidelines",
                        """
                                This includes not engaging in any illegal activities or using bots or other automated tools to manipulate the server.
                                """,
                        false
                )
                .addBlankField(false)
                .addField(
                        "Rule 10: Have fun",
                        """
                                Lastly, enjoy yourself and have fun while following these rules and being part of the community!
                                """,
                        false
                )
                .addBlankField(false)
                .addField(
                        "If you have any questions, please contact a moderator or an administrator.",
                        """
                                Thank you for reading the rules and we hope you enjoy your stay!
                                You can now proceed to the %s channel.
                                """.formatted(channelService
                                .getChannelByName(event, "verify")
                                .map(TextChannel::getAsMention)
                                .orElse("general")),
                        false
                )
                .addBlankField(false)
                .addBlankField(false)
                .addField(
                        "Verify ✅",
                        """
                                Please click the button below to verify yourself.
                                """,
                        false
                )
                .setTimestamp(LocalDateTime.now(ZoneId.of("UTC")))
                .setFooter(format("Created by %s", event.getJDA().getSelfUser().getAsTag()), event.getJDA().getSelfUser().getAvatarUrl());

        getMessageBuilder()
                .setActionRows(ActionRow.of(verifyButton))
                .setEmbeds(getEmbedBuilder().build());

        event.getGuild()
                .getTextChannels()
                .stream()
                .findAny()
                .flatMap(category -> channelService.getChannelByName(event, "rules"))
                .ifPresent(channel -> {
                    channel.sendMessage(getMessageBuilder().build())
                            .queue();
                    log.info("Rules message has been sent.");
                });
    }
}
