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
 *
 */

package io.github.pitzzahh.listeners;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import io.github.pitzzahh.CommandManager;
import org.jetbrains.annotations.NotNull;
import io.github.pitzzahh.Bot;
import java.util.Collection;
import java.util.Arrays;

public class MessageListener extends ListenerAdapter {

    private final CommandManager MANAGER = new CommandManager();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        var user = event.getAuthor();

        var prefix = Bot.getConfig().get("PREFIX");
        var message = event.getMessage().getContentRaw();

       if (message.startsWith(prefix)) {
           final var COMMAND_USED = message.replace(";","").split("\\s");
           final var SIZE = Arrays.stream(COMMAND_USED)
                   .map(Arrays::asList)
                   .mapToLong(Collection::size)
                   .sum();
           var exist = false;
           if (SIZE == 2) {
               exist = MANAGER.getCOMMANDS()
                       .stream()
                       .anyMatch(command -> command.name().equals(COMMAND_USED[1]));
           }

           if (exist || SIZE == 1) MANAGER.handle(event);
           else {
               event.getChannel().sendMessageFormat("%s is not a command", COMMAND_USED[1]).queue();
               event.getChannel().sendMessage(";help").queue();
           }
       }
    }
}
