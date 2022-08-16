package io.github.pitzzahh.events;

import org.jetbrains.annotations.NotNull;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class MessageListener extends ListenerAdapter {

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
