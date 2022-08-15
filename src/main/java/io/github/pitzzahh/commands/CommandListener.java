package io.github.pitzzahh.commands;

import com.github.pitzzahh.utilities.SecurityUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import io.github.pitzzahh.Util;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class CommandListener extends ListenerAdapter {
    private final Random RANDOM = new Random();
    private Set<Integer> pickedJokes = new HashSet<>();
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        var command = event.getName();
        switch (command) {
            case "joke" -> {
                var pick = RANDOM.nextInt(Util.JOKES.length);
                if (pickedJokes.size() == Util.JOKES.length) pickedJokes.clear();
                while (pickedJokes.contains(pick)) pick = RANDOM.nextInt(Util.JOKES.length);
                pickedJokes.add(pick);
                var joke = Util.JOKES[pick].split("[?]");
                var question = SecurityUtil.decrypt(joke[0]);
                event.reply(question + "?").queue();
                try {
                    Thread.sleep(Util.getDelay(question));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                event.getChannel().sendMessage(SecurityUtil.decrypt(joke[1])).queue();
                event.getChannel().sendMessage(":rofl:").queue();
            }
            case "sum" -> {
                var firstNumber = event.getOption("firstnumber");
                var secondNumber = event.getOption("secondnumber");

                if(firstNumber == null || secondNumber == null) return;

                var sum = firstNumber.getAsInt() + secondNumber.getAsInt();

                event.reply(String.format("The sum is: %s", "true")).queue();
            }
            case "terminate" -> {

                var isOwner = event.getHook().getInteraction().getMember().isOwner();
                if (isOwner) {
                    event.reply("THE BOT IS NOW OFFLINE").queue();
                    System.exit(0);
                }
                else {
                    event.reply("YOU DON'T HAVE PERMISSIONS TO SHUTDOWN THE BOT\nYOU CANNOT SEND MESSAGES FOR 1 MINUTE")
                            .queue();
                    var userId = event.getInteraction()
                                        .getMember()
                                        .getId();

                    var standard = event.getGuild().getRolesByName("standard", false).stream().findAny().get();
                    var verified = event.getGuild().getRolesByName("verified", false).stream().findAny().get();

                    event.getGuild()
                            .removeRoleFromMember(UserSnowflake.fromId(userId), verified)
                            .queue();

                    event.getGuild()
                            .addRoleToMember(UserSnowflake.fromId(userId), standard)
                            .queue();
                }
            }
            case "ping" -> {
                final var TIME = System.currentTimeMillis();
                event.reply("Pong!")
                        .setEphemeral(true)
                        .flatMap(v ->
                                event.getHook()
                                        .editOriginalFormat("Pong: %d ms", System.currentTimeMillis() - TIME)
                        ).queue();
            }
        }
    }
}
