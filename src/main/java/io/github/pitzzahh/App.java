package io.github.pitzzahh;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.jetbrains.annotations.NotNull;
import net.dv8tion.jda.api.entities.Activity;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import io.github.pitzzahh.events.MessageListener;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class App extends ListenerAdapter {

    public static void main(@NotNull String[] args) throws LoginException {
        JDA jda = JDABuilder
                .createDefault(args[0])
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setActivity(Activity.listening("your messages \uD83D\uDCE9"))
                .addEventListeners(new MessageListener()).build();
        var guild = jda.getGuildById("1008377788799123496");
        if (guild == null) throw new IllegalStateException("Server ID is not valid");
        guild.upsertCommand("joke", "Tells a random joke").queue();
        guild.upsertCommand("sum", "Sums two given numbers")
                .addOptions(
                        new OptionData(OptionType.INTEGER, "first operand", "the first number", true)
                                .setRequiredRange(1, Integer.MAX_VALUE),
                        new OptionData(OptionType.INTEGER, "second operand", "the second number", true)
                                .setRequiredRange(1, Integer.MAX_VALUE)
                ).queue();
    }
}
