package io.github.pitzzahh.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import com.github.pitzzahh.computing.Calculator;
import com.github.pitzzahh.computing.Operation;
import org.jetbrains.annotations.NotNull;
import net.dv8tion.jda.api.EmbedBuilder;
import java.util.concurrent.TimeUnit;
import io.github.pitzzahh.Util;
import java.math.BigDecimal;
import java.util.*;
import java.awt.*;

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
            case "add" -> {
                var firstNumber = Optional.ofNullable(event.getOption("firstnumber"));
                var secondNumber = Optional.ofNullable(event.getOption("secondnumber"));
                if (firstNumber.isEmpty() || secondNumber.isEmpty()) return;
                var calculator = new Calculator<>();
                calculator.setNumbers(
                        new BigDecimal(firstNumber.get().getAsString()),
                        new BigDecimal(secondNumber.get().getAsString())
                );
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
            case "help" -> {
                var message = new EmbedBuilder();
                message.setColor(Color.BLUE)
                        .setTitle("Available Commands")
                        .appendDescription("slash commands and message commands")
                        .addBlankField(true)
                        .addField(
                                "Slash Commands",
                                "/help\t: Shows how to use the bot.".concat("\n")
                                .concat("/joke\t: Tells a random joker.").concat("\n")
                                .concat("/ping\t: Calculates ping of the bot.").concat("\n")
                                .concat("/add\t: Add two numbers.").concat("\n")
                                .concat("-----------------------------------").concat("\n")
                                ,false)
                        .addField(
                                "Message Commands",
                                "print\t: replies the message that was send".concat("\n")
                                .concat("example -> print hello..the reply will be hello").concat("\n")
                                .concat("hi\t: The bot will greet you with a hello").concat("\n")
                                .concat("hello\t: The bot will greet you with a hello").concat("\n")
                                .concat("-----------------------------------").concat("\n")
                                ,false
                        )
                        .setFooter("THIS MESSAGE WILL BE DELETED AFTER 5 MINUTES");

                event.getChannel()
                        .sendMessageEmbeds(message.build())
                        .queue(e -> e.delete().queueAfter(5, TimeUnit.MINUTES));
            }
        }
    }
}
