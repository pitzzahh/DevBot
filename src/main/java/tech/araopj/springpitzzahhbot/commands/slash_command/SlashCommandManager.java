/*
 * MIT License
 *
 * Copyright (c) 2022 pitzzahh
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
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package tech.araopj.springpitzzahhbot.commands.slash_command;

import tech.araopj.springpitzzahhbot.commands.slash_command.commands.confessions.Confession;
import tech.araopj.springpitzzahhbot.commands.slash_command.commands.game.service.GameService;
import tech.araopj.springpitzzahhbot.commands.slash_command.commands.confessions.service.ConfessionService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import tech.araopj.springpitzzahhbot.commands.slash_command.commands.game.Game;
import tech.araopj.springpitzzahhbot.exceptions.CommandAlreadyExistException;
import tech.araopj.springpitzzahhbot.config.channels.service.ChannelService;
import tech.araopj.springpitzzahhbot.commands.slash_command.commands.Joke;
import tech.araopj.springpitzzahhbot.commands.service.CommandsService;
import tech.araopj.springpitzzahhbot.utilities.MessageUtil;
import org.springframework.stereotype.Component;
import org.jetbrains.annotations.NotNull;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.io.IOException;
import java.util.Objects;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Map;

@Component
public class SlashCommandManager {

    private final Map<String, SlashCommand> COMMANDS = new HashMap<>();

    public SlashCommandManager(
            ConfessionService confessionService,
            CommandsService commandsService,
            ChannelService channelService,
            GameService gameService,
            MessageUtil messageUtil
    ) {
        addCommands(
                new Confession(confessionService, commandsService, channelService, messageUtil),
                new Game(gameService, messageUtil),
                new Joke(messageUtil)
        );
    }

    Consumer<SlashCommand> addCommand = command -> {
        var found = this.COMMANDS.values()
                .stream()
                .anyMatch(c -> c.name().get().equalsIgnoreCase(command.name().get()));
        if (found) throw new CommandAlreadyExistException("A Command With this name is already present!");
        this.COMMANDS.put(command.name().get(), command);
    };

    private void addCommands(@NotNull SlashCommand... commands) {
        Arrays.stream(commands).forEachOrdered(e -> this.addCommand.accept(e));
    }

    public void handle(@NotNull SlashCommandInteractionEvent event) throws IOException, InterruptedException {
        var commandName = event.getName();
        var COMMAND_CONTEXT = new CommandContext(event);
        if (getCommands().containsKey(commandName)) getCommands().get(commandName).execute().accept(COMMAND_CONTEXT);
        var COM = COMMANDS.values()
                .stream()
                .map(SlashCommand::getCommandData)
                .map(Supplier::get)
                .toList();
        if (!COM.isEmpty()) Objects.requireNonNull(event.getGuild()).updateCommands().addCommands(COM).queue();
    }

    public Map<String, SlashCommand> getCommands() {
        return this.COMMANDS;
    }
}
