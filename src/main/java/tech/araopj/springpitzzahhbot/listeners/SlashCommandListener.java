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

package tech.araopj.springpitzzahhbot.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import tech.araopj.springpitzzahhbot.commands.slash_command.SlashCommandManager;
import tech.araopj.springpitzzahhbot.commands.slash_command.SlashCommand;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;
import org.jetbrains.annotations.NotNull;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import java.io.IOException;

@Component
@AllArgsConstructor
public class SlashCommandListener extends ListenerAdapter {

    private final SlashCommandManager SLASH_COMMAND_MANAGER;

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        try {
            SLASH_COMMAND_MANAGER.handle(event);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        final var COMMANDS = SLASH_COMMAND_MANAGER.getCommands();
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
