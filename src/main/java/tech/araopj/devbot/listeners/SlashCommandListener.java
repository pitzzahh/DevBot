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

package tech.araopj.devbot.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import tech.araopj.devbot.commands.slash_commands.SlashCommandManager;
import tech.araopj.devbot.commands.slash_commands.SlashCommand;
import tech.araopj.devbot.services.CommandsService;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class SlashCommandListener extends ListenerAdapter {

    private final SlashCommandManager slashCommandManager;
    private final CommandsService commandsService;

    @Override
    public void onSlashCommandInteraction(@NonNull SlashCommandInteractionEvent event) {
        slashCommandManager.handle(event);
    }

    @Override
    public void onGuildReady(@NonNull GuildReadyEvent event) {
        final var COMMANDS = commandsService.slashCommands();
        var guild = event.getGuild();
        var COM = COMMANDS.values()
                .stream()
                .map(SlashCommand::getCommandData)
                .map(Supplier::get)
                .toList();

        guild.getJDA().updateCommands()
                .addCommands(COM)
                .queue();
    }
}
