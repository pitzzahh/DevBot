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
package io.github.pitzzahh.command.commands;

import com.besaba.revonline.pastebinapi.impl.factory.PastebinFactory;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import com.besaba.revonline.pastebinapi.paste.PasteVisiblity;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import com.besaba.revonline.pastebinapi.paste.PasteExpire;
import com.besaba.revonline.pastebinapi.Pastebin;
import io.github.pitzzahh.command.CommandContext;
import io.github.pitzzahh.command.Command;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import io.github.pitzzahh.Bot;
import java.awt.*;

public class PasteCommand implements Command {

    private final PastebinFactory FACTORY = new PastebinFactory();
    private final Pastebin PASTEBIN = FACTORY.createPastebin(Bot.getConfig().get("DEV_KEY"));
    private final MessageBuilder MESSAGE_BUILDER = new MessageBuilder();
    private final EmbedBuilder EMBED_BUILDER = new EmbedBuilder();

    /**
     * Handles the command.
     *
     * @param context a {@code CommandContext}.
     * @see CommandContext
     */
    @Override
    public void handle(CommandContext context) {
        final var ARGS = context.getArgs();
        final var CHANNEL = context.getEvent().getChannel();

        if (ARGS.size() < 2) {
            final var BUTTON = Button.primary("ok", "okay");
            MESSAGE_BUILDER.clear()
                    .append("MISSING CONTENT")
                    .setActionRows(
                            ActionRow.of(BUTTON)
                    );
            CHANNEL.sendMessage(MESSAGE_BUILDER.build()).queue();
            return;
        }
        final var LANGUAGE = ARGS.get(0);
        final var MESSAGE = context.getEvent().getMessage().getContentRaw();
        final var INDEX = MESSAGE.indexOf(LANGUAGE) + LANGUAGE.length();
        final var CONTENT = MESSAGE.substring(INDEX).trim();

        // get a paste builder to build the paste I want to publish
        final var PASTE_BUILDER = FACTORY.createPaste();

        PASTE_BUILDER
                .setTitle("PASTE")
                .setRaw(CONTENT) // What will be inside the paste?
                .setMachineFriendlyLanguage(LANGUAGE) // Which syntax will use the paste?
                .setVisiblity(PasteVisiblity.Public)  // What is the visibility of this paste?
                .setExpire(PasteExpire.TenMinutes);  // When the paste will expire?

        final var PASTE = PASTE_BUILDER.build();

        final var RESULT = PASTEBIN.post(PASTE); // TODO: fix error cannot connect to pastebin endpoint.

        if (RESULT.hasError()) {
            EMBED_BUILDER.clear()
                    .clearFields()
                    .setColor(Color.RED.brighter())
                    .setTitle(String.format("ERROR: %s", RESULT.getError()));
            context.getEvent().getChannel().sendMessageEmbeds(EMBED_BUILDER.build()).queue();
            return;
        }

        EMBED_BUILDER.clear()
                .clearFields()
                .setColor(Color.BLUE.brighter())
                .setTitle("From: ", RESULT.get())
                .setDescription("```")
                .appendDescription(PASTE.getMachineFriendlyLanguage())
                .appendDescription("\n")
                .appendDescription(PASTE.getRaw().get())
                .appendDescription("```");

        CHANNEL.sendMessageEmbeds(EMBED_BUILDER.build()).queue();
    }

    /**
     * The name of the command.
     *
     * @return the name of the command.
     */
    @Override
    public String name() {
        return "paste";
    }

    /**
     * The description of the command.
     *
     * @return the description of the command.
     */
    @Override
    public String description() {
        return "Creates a paste on the pastebin\n" +
                "Usage: ;paste [language] [content]";
    }
}
