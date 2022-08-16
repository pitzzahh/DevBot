package io.github.pitzzahh;

import java.awt.*;
import java.io.IOException;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Activity;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import io.github.pitzzahh.events.MessageListener;
import net.dv8tion.jda.api.requests.GatewayIntent;
import com.github.pitzzahh.utilities.SecurityUtil;
import io.github.pitzzahh.commands.CommandListener;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class App extends ListenerAdapter {

    public static void main(String[] args) throws LoginException, InterruptedException, IOException {
        if (args.length == 0) throw new IllegalStateException("TOKEN NOT PROVIDED");
        var jda = JDABuilder
                .createDefault(args[0])
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setActivity(Activity.listening("your messages \uD83D\uDCE9"))
                .addEventListeners(new CommandListener())
                .addEventListeners(new MessageListener())
                .build()
                .awaitReady();
        if (args.length == 1) throw new IllegalStateException("GUILD ID IS NOT PROVIDED");
        var server = jda.getGuildById(args[1]);
        if (server == null) throw new IllegalStateException("Server ID is Invalid!");
        else {
            Util.loadJokes();
            server.upsertCommand(Commands.slash("joke", "Tells a random joker"));
            server.upsertCommand(Commands.slash("terminate", "Shutdown the bot"));
            server.upsertCommand(Commands.slash("ping", "Calculate ping of the bot"));
            server.upsertCommand(Commands.slash("sum", "Add two numbers")
                    .addOptions(
                            new OptionData(OptionType.STRING, "firstnumber", "the first number", true),
                            new OptionData(OptionType.STRING, "secondnumber", "the second number", true)
                    ));
            server.retrieveCommands().queue();
        }
    }

    /**
     * Sends a message to verify the user.
     * @param server a {@code Guild} object.
     */
    private static void sendVerifyMessage(Guild server) {
        var embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.CYAN);
        embedBuilder.setTitle("Verify yourself!");
        embedBuilder.addField("How?","Press the agree button to verify", true);
        embedBuilder.setFooter("Created by pitzzahh-bot#3464", server.getIconUrl());

        server.getTextChannelById(SecurityUtil.decrypt("MTAwODY1NzI5NzIyNjA4ODQ2MA=="))
                .sendMessageEmbeds(embedBuilder.build())
                .queue(e -> e.addReaction(Emoji.fromUnicode("✅")).queue());
    }
}
