/*
 * MIT License
 *
 * Copyright (c) 2022 Peter John Arao
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package tech.araopj.devbot;

import tech.araopj.devbot.commands.slash_commands.commands.joke.ViewSubmittedJokes;
import tech.araopj.devbot.services.slash_commands.ConfessionService;
import tech.araopj.devbot.commands.slash_commands.commands.joke.ApproveJoke;
import tech.araopj.devbot.commands.slash_commands.commands.joke.SubmitJoke;
import tech.araopj.devbot.services.slash_commands.JokesService;
import tech.araopj.devbot.services.slash_commands.GameService;
import tech.araopj.devbot.commands.slash_commands.commands.Confession;
import tech.araopj.devbot.commands.slash_commands.commands.joke.GetJoke;
import tech.araopj.devbot.commands.chat_commands.commands.FormatChatCommand;
import tech.araopj.devbot.services.configs.MessageCheckerService;
import tech.araopj.devbot.commands.chat_commands.commands.HelpChatCommand;
import tech.araopj.devbot.commands.chat_commands.commands.PingChatCommand;
import tech.araopj.devbot.commands.slash_commands.SlashCommandManager;
import tech.araopj.devbot.services.configs.ViolationService;
import tech.araopj.devbot.commands.slash_commands.commands.Play;
import tech.araopj.devbot.commands.chat_commands.ChatCommandManager;
import tech.araopj.devbot.services.configs.CategoryService;
import tech.araopj.devbot.services.configs.ChannelService;
import tech.araopj.devbot.services.MessageUtilService;
import tech.araopj.devbot.services.CommandsService;
import tech.araopj.devbot.listeners.SlashCommandListener;
import tech.araopj.devbot.services.configs.TokenService;
import tech.araopj.devbot.listeners.MessageListener;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import tech.araopj.devbot.listeners.ButtonListener;
import tech.araopj.devbot.listeners.MemberLogger;
import tech.araopj.devbot.configs.HttpConfig;
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
