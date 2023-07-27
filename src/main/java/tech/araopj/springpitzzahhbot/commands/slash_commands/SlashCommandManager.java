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

package tech.araopj.springpitzzahhbot.commands.slash_commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import tech.araopj.springpitzzahhbot.exceptions.CommandAlreadyExistException;
import tech.araopj.springpitzzahhbot.services.CommandsService;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import java.util.Objects;

@Slf4j
@Component
public record SlashCommandManager(CommandsService commandsService) {

    public void addCommand(@NonNull SlashCommand slashCommand) {
        var found = commandsService
                .slashCommands()
                .values()
                .stream()
                .anyMatch(c -> c.name().get().equalsIgnoreCase(slashCommand.name().get()));
        if (found) throw new CommandAlreadyExistException("A Command With this name is already present!");
        commandsService
                .slashCommands().put(slashCommand.name().get(), slashCommand);
    }

    public void handle(@NonNull SlashCommandInteractionEvent event) {
        var commandName = event.getName();
        var COMMAND_CONTEXT = new CommandContext(event);
        if (commandsService.slashCommands().containsKey(commandName)) {
            commandsService
                    .slashCommands()
                    .get(commandName)
                    .execute()
                    .accept(COMMAND_CONTEXT);
        }
        var commandData = commandsService
                .slashCommands()
                .values()
                .stream()
                .map(SlashCommand::getCommandData)
                .map(Supplier::get)
                .toList();
        if (!commandData.isEmpty()) {
            Objects.requireNonNull(
                    event.getGuild()
            ).updateCommands()
                    .addCommands(commandData)
                    .queue();
        }
    }

}
