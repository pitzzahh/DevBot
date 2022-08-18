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
package io.github.pitzzahh.listeners;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.EmbedBuilder;
import java.util.concurrent.TimeUnit;
import java.time.LocalDateTime;
import java.util.Objects;
import java.time.ZoneId;
import java.awt.*;

public class ButtonListener extends ListenerAdapter {

    private final EmbedBuilder EMBED_BUILDER = new EmbedBuilder();

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        final var ID = event.getButton().getId();
        final var USER = event.getUser();
        System.out.println("ID = " + ID);
        if ("ok".equals(ID)) event.getInteraction().getMessage().delete().queue();
        else if ("verify-button".equals(ID)) {
            final var VERIFIED_ROLE = Objects.requireNonNull(event.getGuild()).getRolesByName("verified", false).stream().findAny();
            if (VERIFIED_ROLE.isPresent()) {
                var isVerified = USER.getJDA().getRoles()
                        .stream()
                        .map(Role::getName)
                        .anyMatch(e -> e.equals(VERIFIED_ROLE.get().getName()));
                if (isVerified) {
                    EMBED_BUILDER.clear()
                            .clearFields()
                            .setColor(Color.RED)
                            .setTitle("Already Verified")
                            .setTimestamp(LocalDateTime.now(ZoneId.of("UTC")).plusMinutes(1))
                            .setFooter("This message will be automatically deleted");
                    event.getInteraction()
                            .replyEmbeds(EMBED_BUILDER.build())
                            .setEphemeral(true)
                            .queue(e -> e.deleteOriginal().queueAfter(1, TimeUnit.MINUTES));
                }
                else {
                    EMBED_BUILDER.clear()
                            .clearFields()
                            .setColor(Color.BLUE.brighter())
                            .setTitle("Verified ✅")
                            .setTimestamp(LocalDateTime.now(ZoneId.of("UTC")).plusMinutes(1))
                            .setFooter("This message will be automatically deleted");

                    event.getGuild().addRoleToMember(USER, VERIFIED_ROLE.get()).queue();
                    event.getInteraction()
                            .replyEmbeds(EMBED_BUILDER.build())
                            .queue(e -> e.deleteOriginal().queueAfter(1, TimeUnit.MINUTES));
                }
            }
        }
    }
}
