package io.github.pitzzahh.events;

import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class Verifier extends ListenerAdapter {

    /**
     * @param event
     */
    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if (event.getEmoji().getName().equals("✅")) {
            var channel = event
                    .getGuild()
                    .getTextChannelById("1008657297226088460");
            var message = channel
                    .getLatestMessageId();
            if (message.equals("1009013499294396496")) {
                var user = event.getUser();
                var role = event.getGuild().getRolesByName("verified", false).stream().findAny().get();
                event.getGuild().addRoleToMember(user, role).queue();
            }
        }
    }

    /**
     * Removed verfied role to a user when reaction is removed.
     * @param event
     */
    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        if (event.getEmoji().getName().equals("✅")) {
            var channel = event
                    .getGuild()
                    .getTextChannelById("1008657297226088460");
            var message = channel
                    .getLatestMessageId();
            if (message.equals("1009013499294396496")) {
                var user = event.getUser();
                var role = event.getGuild().getRolesByName("verified", false).stream().findAny().get();
                event.getGuild().removeRoleFromMember(user, role).queue();
            }
        }
    }
}
