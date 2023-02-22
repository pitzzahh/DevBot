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
package tech.araopj.springpitzzahhbot.commands.chat_command.commands;

import tech.araopj.springpitzzahhbot.commands.chat_command.CommandContext;
import tech.araopj.springpitzzahhbot.commands.chat_command.Command;
import org.springframework.stereotype.Component;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Component
public class PingCommand implements Command {

    /**
     * Contains the process to be handled.
     *
     * @param context a {@code CommandContext}.
     * @see CommandContext
     */
    public void  process(CommandContext context) {
        var jda = context.getEvent().getJDA();
        jda.getRestPing().queue(
                ping -> context.getEvent()
                                .getChannel()
                                .sendMessageFormat("Pong: %sms", ping)
                                .queue()
        );
    }

    /**
     * Handles the chat_command.
     * Accepts a {@code CommandContext}.
     *
     * @see CommandContext
     */
    @Override
    public Consumer<CommandContext> handle() {
        return this::process;
    }

    @Override
    public Supplier<String> name() {
        return () -> "ping";
    }

    /**
     * The description of the chat_command.
     *
     * @return the description of the chat_command.
     */
    @Override
    public Supplier<String> description() {
        return () -> "Shows the current ping from the bot to the discord servers.";
    }
}
