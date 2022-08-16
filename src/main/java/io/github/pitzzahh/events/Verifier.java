package io.github.pitzzahh.events;

import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;

public class Verifier extends ListenerAdapter {

    /**
     * Sets a {@code verified} role to a user when the user reacts to the verify message.
     * @param event the event that occured.
     */
    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        var channel = event.getChannel().asTextChannel();
        if (channel.getId().equals("1008657297226088460")) { // TODO: encrypt channel id
            if (event.getEmoji().getName().equals("✅")) {
                var message = event.getMessageId();
                if (message.equals("1009060774901207221")) { // TODO: encrypt message id
                    var user = event.getUser();
                    var role = event.getGuild()
                            .getRolesByName("verified", false)
                            .stream()
                            .findAny()
                            .get();
                    event.getGuild()
                            .addRoleToMember(user, role)
                            .queue();
                }
            }
        }
    }

    /**
     * Removes a {@code verified} role to a user when the user remove his/her reaction to the verify message.
     * @param event the event that occured.
     */
    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        var channel = event.getChannel().asTextChannel();
        if (channel.getId().equals("1008657297226088460")) { // TODO: encrypt channel id
            if (event.getEmoji().getName().equals("✅")) {
                var message = event.getMessageId();
                if (message.equals("1009060774901207221")) { // TODO: encrypt message id
                    var user = Optional.ofNullable(event.getUser());
                    var role = event.getGuild()
                            .getRolesByName("verified", false)
                            .stream()
                            .findAny()
                            .get();
                    if (user.isPresent()) {
                        event.getGuild()
                                .removeRoleFromMember(user.get(), role)
                                .queue();
                    }
                }
            }
        }
    }
}
