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

import static io.github.pitzzahh.utilities.IChannels.MEMBER_UPDATES_CHANNEL;
import static io.github.pitzzahh.utilities.IChannels.getChannelByName;
import static io.github.pitzzahh.utilities.Util.EMBED_BUILDER;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import static io.github.pitzzahh.utilities.ICategory.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Member;
import static java.time.LocalDateTime.now;
import org.jetbrains.annotations.NotNull;
import static java.lang.String.format;
import static java.time.ZoneId.of;
import static java.awt.Color.*;

public class MemberLogger extends ListenerAdapter {

    /**
     * Greets a new member that joined the server.
     * @param event the event.
     */
    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        event.getGuild()
                .getCategoriesByName(MEMBER_UPDATES_CATEGORY, true)
                .stream()
                .findAny()
                .flatMap(category -> getChannelByName(event, MEMBER_UPDATES_CHANNEL))
                .ifPresent(channel -> joining(channel, event, event.getGuild().getSelfMember()));
    }

    /**
     * Sends an info message that a user leaved the server.
     * @param event the event.
     */
    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        event.getGuild()
                .getCategoriesByName(MEMBER_UPDATES_CATEGORY, true)
                .stream()
                .findAny()
                .flatMap(category -> getChannelByName(event, MEMBER_UPDATES_CHANNEL))
                .ifPresent(channel -> leaving(channel, event, event.getGuild().getSelfMember()));
    }

    /**
     * Logic
     * @param channel the text channel.
     * @param event the event occurred.
     * @param member the member who leaved the guild.
     */
    private void joining(TextChannel channel, GenericGuildEvent event, Member member) {
        message(event, member, true);
        channel.sendMessageEmbeds(EMBED_BUILDER.build()).queue();
    }

    /**
     * Logic
     * @param channel the text channel.
     * @param event the event occurred.
     * @param member the member who leaved the guild.
     */
    private void leaving(TextChannel channel, GenericGuildEvent event, Member member) {
        message(event, member, false);
        channel.sendMessageEmbeds(EMBED_BUILDER.build()).queue();
        removeRole(event, member);
    }

    private void removeRole(GenericGuildEvent event, Member member) {
        final var ROLE = event.getGuild().getRolesByName("verified", false).stream().findAny();
        ROLE.ifPresent(
                role ->  event.getGuild()
                        .removeRoleFromMember(member, ROLE.get())
                        .queue()
        );
    }

    /**
     * Creates an embedded message for guild join or leave message.
     * @param event the event occurred.
     * @param member the member who leaved the guild.
     * @param flag if {@code true} a member joined the server.
     */
    public void message(GenericGuildEvent event, Member member, boolean flag) {
        EMBED_BUILDER.clear()
                .clearFields()
                .setColor(flag ? GREEN : RED)
                .setTitle(format(flag ? "%s Joined the Server!" : "%s Leaved the Server💔" , member.getEffectiveName()))
                .appendDescription(flag ? "joined" : "farewell")
                .setTimestamp(now(of("UTC")))
                .setFooter(
                        format("Created by %s", event.getJDA().getSelfUser().getAsTag()),
                        event.getJDA().getSelfUser().getAvatarUrl()
                );
    }

}
