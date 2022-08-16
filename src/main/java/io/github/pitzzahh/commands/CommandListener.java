package io.github.pitzzahh.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import com.github.pitzzahh.computing.Calculator;
import com.github.pitzzahh.computing.Operation;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.TimeUnit;
import io.github.pitzzahh.Util;
import java.util.HashSet;
import java.util.Arrays;
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
                var pick = RANDOM.nextInt(Util.JOKES.size());
                if (pickedJokes.size() == Util.JOKES.size()) pickedJokes.clear();
                while (pickedJokes.contains(pick)) pick = RANDOM.nextInt(Util.JOKES.size());
                pickedJokes.add(pick);
                var joke = Util.JOKES.get(pick);
                System.out.println("joke = " + Arrays.toString(joke));
                var question = joke[0];
                event.reply(question + "?").queue();
                Util.sleep(Util.getDelay(question));
                event.getChannel().sendMessage(joke[1]).queue();
                event.getChannel().sendMessage(":rofl:").queue();
            }
            case "sum" -> {
                var firstNumber = event.getOption("firstnumber");
                var secondNumber = event.getOption("secondnumber");
                var calculator = new Calculator<>();
                calculator.setNumbers(firstNumber.getAsInt(), secondNumber.getAsInt());
                try {
                    var sum = calculator.calculate(Operation.ADD)
                            .getCalculation();
                    event.reply(String.format("The sum is: %s", sum.toString())).queue();
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            case "terminate" -> {

                var isOwner = event.getHook().getInteraction().getMember().isOwner();
                if (isOwner) {
                    event.reply("THE BOT IS NOW OFFLINE").queue();
                    Util.sleep(1000);
                    System.exit(0);
                }
                else {
                    event.reply("YOU DON'T HAVE PERMISSIONS TO SHUTDOWN THE BOT\nYOU CANNOT SEND MESSAGES FOR 2 MINUTES")
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
                            .modifyMemberRoles(event.getInteraction().getMember(), standard)
                            .queue();

                    var isDone = event.getGuild()
                            .addRoleToMember(UserSnowflake.fromId(userId), verified)
                            .queueAfter(2, TimeUnit.MINUTES).isDone();

                    if (isDone) event.getGuild().removeRoleFromMember(UserSnowflake.fromId(userId), standard).queue();
                }
            }
            case "ping" -> {
                final var TIME = System.currentTimeMillis();
                event.reply("Pong!")
                        .setEphemeral(true)
                        .flatMap(v -> v.editOriginalFormat("Pong: %d ms", System.currentTimeMillis() - TIME))
                        .queue();
            }
        }
    }
}
