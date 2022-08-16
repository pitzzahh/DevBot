package io.github.pitzzahh.events;

import java.awt.*;
import io.github.pitzzahh.Util;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
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
                default -> {
                    var m = event.getMessage().getContentRaw();
                    var isBad = Util.BAD_WORDS
                            .stream()
                            .anyMatch(e -> e.equalsIgnoreCase(m));
                    System.out.println("m = " + m);
                    System.out.println("isBad = " + isBad);
                    if (isBad) {
                        var message = new EmbedBuilder();
                        message.setColor(Color.RED)
                                .setTitle("PEASANT ALERT!")
                                .setDescription(String.format("This message seems sus..", event.getMessage().getContentRaw()))
                                .setDescription("your suss message will be deleted after 5 seconds")
                                .setTimestamp(LocalDateTime.now())
                                .setFooter("this message will be deleted after 1 minute");

                        event.getMessage()
                                .replyEmbeds(message.build())
                                .queue(e -> {
                                    event.getMessage().delete()
                                            .reason("BAD WORDS ARE NOT ALLOWED")
                                            .queueAfter(5, TimeUnit.SECONDS);
                                    e.delete().queueAfter(1, TimeUnit.MINUTES);
                                });
                    }
                }
            }
        }
    }
}
