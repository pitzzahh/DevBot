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
import static java.time.format.DateTimeFormatter.ofLocalizedTime;
import static io.github.pitzzahh.utilities.Util.EMBED_BUILDER;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import static java.time.format.FormatStyle.SHORT;
import static java.time.LocalDateTime.now;
import org.jetbrains.annotations.NotNull;
import net.dv8tion.jda.api.entities.Role;
import static java.lang.String.format;
import static java.time.ZoneId.of;
import static java.awt.Color.BLUE;
import static java.awt.Color.RED;
import java.util.Objects;

public class ButtonListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        final var ID = event.getButton().getId();
        final var MEMBER = event.getMember();
        if ("ok".equals(ID)) event.getInteraction().getMessage().delete().queue();
        else if ("verify-button".equals(ID)) {
            final var VERIFIED_ROLE = Objects.requireNonNull(event.getGuild(), "Cannot find verified role").getRolesByName("verified", false).stream().findAny();
            if (VERIFIED_ROLE.isPresent()) {
                assert MEMBER != null;
                var isVerified = MEMBER.getRoles()
                        .stream()
                        .map(Role::getName)
                        .anyMatch(e -> VERIFIED_ROLE.get().getName().equals(e));
                if (isVerified) {
                    EMBED_BUILDER.clear()
                            .clearFields()
                            .setColor(RED)
                            .setTitle("Already Verified")
                            .setFooter(
                                    format(
                                            "This message will be automatically deleted on %s",
                                            now(of("UTC"))
                                                    .plusMinutes(1)
                                                    .format(ofLocalizedTime(SHORT))
                                    )
                            );
                }
                else {
                    EMBED_BUILDER.clear()
                            .clearFields()
                            .setColor(BLUE)
                            .setTitle("Verified ✅")
                            .setFooter(format("This message will be automatically deleted on %s",
                                            now(of("UTC"))
                                                    .plusMinutes(1)
                                                    .format(ofLocalizedTime(SHORT))
                                    )
                            );
                    event.getGuild().addRoleToMember(MEMBER, VERIFIED_ROLE.get()).queue();
                }
                event.getInteraction()
                        .replyEmbeds(EMBED_BUILDER.build())
                        .setEphemeral(true)
                        .queue();
            }
        }
    }
}
