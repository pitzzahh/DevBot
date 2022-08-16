package io.github.pitzzahh.events;

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import com.github.pitzzahh.utilities.SecurityUtil;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class UserLogger extends ListenerAdapter {

    /**
     * Sends a message on new-commers text channel when a user joined the server.
     * @param event the event used.
     */
    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        var api = event.getJDA();
        var user = event.getUser();

        var guild = api.getGuildById(SecurityUtil.decrypt("MTAwODY1NzI5NzIyNjA4ODQ1OA=="));

        guild.getTextChannelById(SecurityUtil.decrypt("MTAwODk1NDEyNzYzMzYyMTA0Mg=="))
                .sendMessageFormat("%s joined the server!\nThanks for joining us", user.getName()).queue();
    }
}