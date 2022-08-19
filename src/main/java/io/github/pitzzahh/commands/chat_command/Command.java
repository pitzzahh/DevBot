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
package io.github.pitzzahh.commands.chat_command;

import java.util.List;

/**
 * Interface used to handle commands.
 */
public interface Command {

    /**
     * Handles the chat_command.
     * @param context a {@code CommandContext}.
     * @see CommandContext
     */
    void handle(CommandContext context);

    /**
     * The name of the chat_command.
     * @return the name of the chat_command.
     */
    String name();

    /**
     * The description of the chat_command.
     * @return the description of the chat_command.
     */
    String description();

    /**
     * The possible aliases for a chat_command.
     * @return a {@code List<String>} containing the aliases of a chat_command.
     */
    default List<String> aliases() {
        return List.of();
    }


}
