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

package io.github.pitzzahh.utilities;

import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.entities.TextChannel;
import static io.github.pitzzahh.Bot.getConfig;
import java.util.Optional;

public interface IChannels {

    String VERIFY_CHANNEL_NAME = getConfig.get().get("VERIFY_CHANNEL_NAME");

    String MEMBER_UPDATES_CHANNEL = getConfig.get().get("MEMBER_UPDATES_CHANNEL");

    String ENTER_SECRET_CHANNEL = getConfig.get().get("ENTER_SECRET_CHANNEL");

    String SENT_SECRET_CHANNEL = getConfig.get().get("SENT_SECRET_CHANNEL");

    static Optional<TextChannel> getChannelByName(GenericGuildEvent event, String name) {
        return event
                .getGuild()
                .getTextChannelsByName(name, true)
                .stream()
                .findAny();
    }

}
