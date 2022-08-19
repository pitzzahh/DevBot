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

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.MessageBuilder;
import io.github.pitzzahh.CommandManager;
import org.jetbrains.annotations.NotNull;
import net.dv8tion.jda.api.EmbedBuilder;
import java.time.LocalDateTime;
import io.github.pitzzahh.Bot;
import java.time.ZoneId;
import java.awt.*;
public class MessageListener extends ListenerAdapter {

    private final CommandManager MANAGER = new CommandManager();
    private final MessageBuilder MESSAGE_BUILDER = new MessageBuilder();
    private final EmbedBuilder EMBED_BUILDER = new EmbedBuilder();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        final var AUTHOR = event.getAuthor();
        final var PREFIX = Bot.getConfig().get("PREFIX");
        final var MESSAGE = event.getMessage().getContentRaw();
        if (MESSAGE.startsWith(PREFIX)) MANAGER.handle(event);
        else {
            if (MESSAGE.equals(Bot.getConfig().get("VERIFY_MESSAGE"))) {
                final var IS_IN_VERIFY_CHANNEL = event.getChannel()
                        .getName()
                        .equals(Bot.getConfig().get("VERIFY_CHANNEL"));
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
                            .queue(e -> event.getMessage().delete().queue());
                }
            }
        }
    }
}
