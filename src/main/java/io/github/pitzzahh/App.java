package io.github.pitzzahh;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import io.github.pitzzahh.events.MessageListener;
import net.dv8tion.jda.api.requests.GatewayIntent;
import io.github.pitzzahh.commands.CommandListener;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class App extends ListenerAdapter {

    public static void main(String[] args) throws LoginException {
        if (args.length == 0) throw new IllegalStateException("TOKEN NOT PROVIDED");
        var jda = JDABuilder
                .createDefault(args[0])
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setActivity(Activity.listening("your messages \uD83D\uDCE9"))
                .addEventListeners(new CommandListener())
                .addEventListeners(new MessageListener())
                .build();

        var commands = jda.updateCommands();
        commands.addCommands(Commands.slash("joke", "Tells a random joker"));
        commands.addCommands(Commands.slash("sum", "Add two numbers")
                .addOptions(
                        new OptionData(OptionType.INTEGER, "firstnumber", "the first number", true)
                                .setRequiredRange(1, Integer.MAX_VALUE),
                        new OptionData(OptionType.INTEGER, "secondnumber", "the second number", true)
                                .setRequiredRange(1, Integer.MAX_VALUE)
                ));
        commands.queue();
    }
}
