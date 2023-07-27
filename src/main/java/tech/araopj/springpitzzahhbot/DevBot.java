package tech.araopj.springpitzzahhbot;

import tech.araopj.springpitzzahhbot.commands.slash_commands.commands.joke.ViewSubmittedJokes;
import tech.araopj.springpitzzahhbot.services.slash_commands.ConfessionService;
import tech.araopj.springpitzzahhbot.commands.slash_commands.commands.joke.ApproveJoke;
import tech.araopj.springpitzzahhbot.commands.slash_commands.commands.joke.SubmitJoke;
import tech.araopj.springpitzzahhbot.services.slash_commands.JokesService;
import tech.araopj.springpitzzahhbot.services.slash_commands.GameService;
import tech.araopj.springpitzzahhbot.commands.slash_commands.commands.Confession;
import tech.araopj.springpitzzahhbot.commands.slash_commands.commands.joke.GetJoke;
import tech.araopj.springpitzzahhbot.commands.chat_commands.commands.FormatChatCommand;
import tech.araopj.springpitzzahhbot.services.configs.MessageCheckerService;
import tech.araopj.springpitzzahhbot.commands.chat_commands.commands.HelpChatCommand;
import tech.araopj.springpitzzahhbot.commands.chat_commands.commands.PingChatCommand;
import tech.araopj.springpitzzahhbot.commands.slash_commands.SlashCommandManager;
import tech.araopj.springpitzzahhbot.services.configs.ViolationService;
import tech.araopj.springpitzzahhbot.commands.slash_commands.commands.Play;
import tech.araopj.springpitzzahhbot.commands.chat_commands.ChatCommandManager;
import tech.araopj.springpitzzahhbot.services.configs.CategoryService;
import tech.araopj.springpitzzahhbot.services.configs.ChannelService;
import tech.araopj.springpitzzahhbot.services.MessageUtilService;
import tech.araopj.springpitzzahhbot.services.CommandsService;
import tech.araopj.springpitzzahhbot.listeners.SlashCommandListener;
import tech.araopj.springpitzzahhbot.services.configs.TokenService;
import tech.araopj.springpitzzahhbot.listeners.MessageListener;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import tech.araopj.springpitzzahhbot.listeners.ButtonListener;
import tech.araopj.springpitzzahhbot.listeners.MemberLogger;
import tech.araopj.springpitzzahhbot.configs.HttpConfig;
import org.springframework.context.annotation.Bean;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.ShardManager;
import javax.security.auth.login.LoginException;
import org.springframework.stereotype.Service;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.OnlineStatus;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;

@Slf4j
@Service
public record DevBot(
        MessageCheckerService messageCheckerService,
        SlashCommandListener slashCommandListener,
        SlashCommandManager slashCommandManager,
        ChatCommandManager chatCommandManager,
        ViewSubmittedJokes viewSubmittedJokes,
        MessageUtilService messageUtilService,
        FormatChatCommand formatChatCommand,
        ConfessionService confessionService,
        ViolationService violationService,
        MessageListener messageListener,
        PingChatCommand pingChatCommand,
        CommandsService commandsService,
        CategoryService categoryService,
        HelpChatCommand helpChatCommand,
        ButtonListener buttonListener,
        ChannelService channelService,
        MemberLogger memberLogger,
        TokenService tokenService,
        JokesService jokesService,
        GameService gameService,
        ApproveJoke approveJoke,
        HttpConfig httpConfig,
        Confession confession,
        SubmitJoke submitJoke,
        GetJoke getJoke,
        Play play
) {

    @Bean
    public ShardManager shardManager() {
        log.info("Initializing ShardManager...");
        var builder = DefaultShardManagerBuilder.createDefault(tokenService.getToken());

        builder.setStatus(OnlineStatus.ONLINE)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .enableIntents(GatewayIntent.GUILD_MESSAGES)
                .enableIntents(GatewayIntent.GUILD_MESSAGE_REACTIONS)
                .enableIntents(GatewayIntent.DIRECT_MESSAGES)
                .enableIntents(GatewayIntent.DIRECT_MESSAGE_REACTIONS)
                .setActivity(Activity.listening("maintenance ⛑️"));

        try {
            messageCheckerService.loadSwearWords();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        builder.addEventListeners(
                messageListener,
                buttonListener,
                slashCommandListener,
                memberLogger
        );
        log.info("ChatCommand manager: {}", chatCommandManager);
        log.info("SlashCommand manager: {}", slashCommandManager);

        chatCommandManager.addCommand(pingChatCommand);
        chatCommandManager.addCommand(formatChatCommand);
        chatCommandManager.addCommand(helpChatCommand);

        slashCommandManager.addCommand(confession);
        slashCommandManager.addCommand(play);
        slashCommandManager.addCommand(getJoke);
        slashCommandManager.addCommand(submitJoke);
        slashCommandManager.addCommand(approveJoke);
        slashCommandManager.addCommand(viewSubmittedJokes);

        log.info("Chat Commands: {}", commandsService.chatCommands());
        log.info("Slash Commands: {}", commandsService.slashCommands());

        try {
            log.info("ShardManager initialized successfully!");
            return builder.build();
        } catch (LoginException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
