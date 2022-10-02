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

import static io.github.pitzzahh.Bot.getConfig;
import static io.github.pitzzahh.utilities.Util.EMBED_BUILDER;

import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import static java.time.LocalDateTime.now;
import org.jetbrains.annotations.NotNull;
import static java.lang.String.format;
import static java.time.ZoneId.of;
import java.awt.*;

public class MemberLogger extends ListenerAdapter {
    private final String CHANNEL_NAME = getConfig.get().get("MEMBER_UPDATES_CHANNEL");
    private final String CATEGORY_NAME = getConfig.get().get("MEMBER_UPDATES_CHANNEL_CATEGORY");
    /**
     * Greets a new member that joined the server.
     * @param event the event
     */
    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        final var MEMBER = event.getGuild().getSelfMember();
        final var CATEGORY = event.getGuild()
                .getCategoriesByName(CATEGORY_NAME, true)
                .stream()
                .findAny();
        CATEGORY.ifPresent(
                category -> {
                    var MEMBER_UPDATES_CHANNEL = event.getGuild()
                            .getTextChannelsByName(CHANNEL_NAME, true)
                            .stream()
                            .findAny();
                    MEMBER_UPDATES_CHANNEL.ifPresent(
                            c -> {
                                EMBED_BUILDER.clear()
                                        .clearFields()
                                        .setColor(Color.BLUE)
                                        .setTitle(String.format("%s Joined the Server!", MEMBER.getEffectiveName()))
                                        .appendDescription("joined")
                                        .setTimestamp(now(of("UTC")))
                                        .setFooter(
                                                format("Created by %s", event.getJDA().getSelfUser().getAsTag()),
                                                event.getJDA().getSelfUser().getAvatarUrl()
                                        );
                                c.sendMessageEmbeds(EMBED_BUILDER.build()).queue();
                            }
                    );
                }
        );
    }

    /**
     * Sends an info message that a user leaved the server.
     * @param event the event.
     */
    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        final var MEMBER = event.getGuild().getSelfMember();
        final var CATEGORY = event.getGuild()
                .getCategoriesByName(CATEGORY_NAME, true)
                .stream()
                .findAny();
        CATEGORY.ifPresent(
                category -> {
                    var MEMBER_UPDATES_CHANNEL = event.getGuild()
                            .getTextChannelsByName(CHANNEL_NAME, true)
                            .stream().findAny();
                    MEMBER_UPDATES_CHANNEL.ifPresent(
                            c -> {
                                EMBED_BUILDER.clear()
                                        .clearFields()
                                        .setColor(Color.RED)
                                        .setTitle(String.format("%s Leaved the Server💔", MEMBER.getEffectiveName()))
                                        .appendDescription("farewell")
                                        .setTimestamp(now(of("UTC")))
                                        .setFooter(
                                                format("Created by %s", event.getJDA().getSelfUser().getAsTag()),
                                                event.getJDA().getSelfUser().getAvatarUrl()
                                        );
                                c.sendMessageEmbeds(EMBED_BUILDER.build()).queue();
                                final var ROLE = event.getGuild().getRolesByName("verified", false).stream().findAny();
                                ROLE.ifPresent(
                                        role ->  event.getGuild().removeRoleFromMember(MEMBER, ROLE.get()).queue()
                                );
                            }
                    );
                }
        );
    }
}
