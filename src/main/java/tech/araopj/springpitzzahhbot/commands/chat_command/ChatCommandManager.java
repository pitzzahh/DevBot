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
package tech.araopj.springpitzzahhbot.commands.chat_command;

import tech.araopj.springpitzzahhbot.exceptions.CommandAlreadyExistException;
import tech.araopj.springpitzzahhbot.commands.service.CommandsService;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;
import lombok.extern.slf4j.Slf4j;
import java.util.regex.Pattern;
import java.util.Optional;
import java.util.Arrays;

@Slf4j
@Component
public record ChatCommandManager(CommandsService commandsService) {

    /**
     * Adds a chat_command.
     * @param chatCommand the chat_command to add.
     */
    public void addCommand(ChatCommand chatCommand) {
        var found = commandsService
                .chatCommands()
                .stream()
                .anyMatch(c -> c.name().get().equalsIgnoreCase(chatCommand.name().get()));
        log.info("Does {} already added: {}", chatCommand.name().get(), found);
        if (found) throw new CommandAlreadyExistException("A ChatCommand With this name is already present!");
        log.info("Adding chat_command: {}", chatCommand.name().get());
        commandsService.chatCommands().add(chatCommand);
    }

    /**
     * Gets a chat_command from the list.
     * accepts a String the name of the chat_command
     * returns a {@code ChatCommand}.
     */
    public Optional<ChatCommand> getChatCommandByName(String name) {
        return commandsService
                .chatCommands()
                .stream()
                .filter(c -> c.name().get().equalsIgnoreCase(name.toLowerCase()) || c.aliases().get().contains(name.toLowerCase()))
                .findAny();
    }


    /**
     * Handles commands.
     * @param event the event that happened.
     */
    public void handle(@NonNull MessageReceivedEvent event) {
        final var SPLIT = event.getMessage().getContentRaw()
                .replaceFirst("(?i)".concat(Pattern.quote(commandsService.getPrefix())), "")
                .split("\\s+");
        final var INVOKED = SPLIT[0].toLowerCase();
        final var COMMAND = getChatCommandByName(INVOKED);
        event.getChannel().sendTyping().queue();
        final var ARGS = Arrays.asList(SPLIT).subList(1, SPLIT.length);
        final var COMMAND_CONTEXT = new CommandContext(event, ARGS);
        COMMAND.ifPresentOrElse(chatCommand -> chatCommand.handle().accept(COMMAND_CONTEXT),
                () -> event.getMessage().reply(String.format("%s is not a chat_command", INVOKED)).queue(
                e -> e.getChannel().sendMessage(";help").queue(m -> m.delete().queue())
        ));
    }

}
