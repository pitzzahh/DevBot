package io.github.pitzzahh.events;

import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import org.jetbrains.annotations.NotNull;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class MessageListener extends ListenerAdapter {

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
                var member = event.getMember();

                var role = event
                        .getGuild()
                        .getRolesByName("verified", false)
                        .stream()
                        .findAny()
                        .get();

                event.getGuild()
                        .removeRoleFromMember(member, role)
                        .queue();
            }
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            var messageSent = event.getMessage().getContentStripped();
            var command = messageSent.split("\\s")[0].toLowerCase();
            switch (command) {
                case "print" -> {
                    var message = messageSent.substring(command.length(), messageSent.length());
                    event.getMessage().reply(message).queue();
                }
                case "hello", "hi" ->  {
                    var user = event.getAuthor().getName();
                    event.getMessage().addReaction(Emoji.fromUnicode("\uD83D\uDC4B")).queue();
                    event.getMessage().reply(String.format("Hello %s!", user)).queue();
                }
            }
        }
    }
}
