package tech.araopj.springpitzzahhbot.service;

import tech.araopj.springpitzzahhbot.commands.slash_command.commands.confessions.service.ConfessionService;
import tech.araopj.springpitzzahhbot.commands.slash_command.commands.joke.approveJoke.ApproveJoke;
import tech.araopj.springpitzzahhbot.commands.slash_command.commands.joke.submitJoke.SubmitJoke;
import tech.araopj.springpitzzahhbot.commands.slash_command.commands.joke.service.JokesService;
import tech.araopj.springpitzzahhbot.commands.slash_command.commands.game.service.GameService;
import tech.araopj.springpitzzahhbot.commands.slash_command.commands.confessions.Confession;
import tech.araopj.springpitzzahhbot.commands.slash_command.commands.joke.getJoke.GetJoke;
import tech.araopj.springpitzzahhbot.commands.chat_command.commands.FormatChatCommand;
import tech.araopj.springpitzzahhbot.config.moderation.service.MessageCheckerService;
import tech.araopj.springpitzzahhbot.commands.chat_command.commands.HelpChatCommand;
import tech.araopj.springpitzzahhbot.commands.chat_command.commands.PingChatCommand;
import tech.araopj.springpitzzahhbot.commands.slash_command.SlashCommandManager;
import tech.araopj.springpitzzahhbot.config.moderation.service.ViolationService;
import tech.araopj.springpitzzahhbot.commands.slash_command.commands.game.Play;
import tech.araopj.springpitzzahhbot.commands.chat_command.ChatCommandManager;
import tech.araopj.springpitzzahhbot.config.category.service.CategoryService;
import tech.araopj.springpitzzahhbot.config.channels.service.ChannelService;
import tech.araopj.springpitzzahhbot.utilities.service.MessageUtilService;
import tech.araopj.springpitzzahhbot.commands.service.CommandsService;
import tech.araopj.springpitzzahhbot.listeners.SlashCommandListener;
import tech.araopj.springpitzzahhbot.config.service.TokenService;
import tech.araopj.springpitzzahhbot.listeners.MessageListener;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import tech.araopj.springpitzzahhbot.listeners.ButtonListener;
import tech.araopj.springpitzzahhbot.listeners.MemberLogger;
import tech.araopj.springpitzzahhbot.config.HttpConfig;
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
public record BotService(
        MessageCheckerService messageCheckerService,
        SlashCommandListener slashCommandListener,
        SlashCommandManager slashCommandManager,
        ChatCommandManager chatCommandManager,
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
