package io.github.pitzzahh.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import io.github.pitzzahh.Util;
import java.util.Random;

public class CommandListener extends ListenerAdapter {
    private final Random RANDOM = new Random();
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        var command = event.getName();
        switch (command) {
            case "joke" -> {
                var pick = RANDOM.nextInt(Util.JOKES.length);
                var joke = Util.JOKES[pick].split("[?]");
                event.reply(joke[0] + "?").queue();
                try {
                    Thread.sleep(Util.getDelay(joke[0]));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                event.getChannel().sendMessage(joke[1]).queue();
                event.getChannel().sendMessage(":rofl:").queue();
            }
            case "sum" -> {
                var firstNumber = event.getOption("firstnumber");
                var secondNumber = event.getOption("secondnumber");

                if(firstNumber == null || secondNumber == null) return;

                var sum = firstNumber.getAsInt() + secondNumber.getAsInt();

                event.reply("The sum is: " + sum).queue();
            }
        }
    }
}
