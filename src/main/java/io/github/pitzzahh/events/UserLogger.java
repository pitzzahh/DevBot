package io.github.pitzzahh.events;

import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import com.github.pitzzahh.utilities.SecurityUtil;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class UserLogger extends ListenerAdapter {

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        var api = event.getJDA();
        var user = api.getUserById(api.getSelfUser().getId()); // Acquire a reference to the User instance through the id

        var guild = api.getGuildById(SecurityUtil.decrypt("MTAwODY1NzI5NzIyNjA4ODQ1OA=="));

        guild.getTextChannelById(SecurityUtil.decrypt("MTAwODk1NDEyNzYzMzYyMTA0Mg=="))
                .sendMessageFormat("%s joined the server!\nThanks for joining us", user.getName()).queue();
    }
}