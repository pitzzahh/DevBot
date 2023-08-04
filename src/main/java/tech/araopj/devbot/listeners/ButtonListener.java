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

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import tech.araopj.devbot.services.MessageUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import static java.util.concurrent.TimeUnit.MINUTES;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;
import static java.awt.Color.RED;
import lombok.extern.slf4j.Slf4j;
import java.util.Objects;

@Slf4j
@Component
public class ButtonListener extends ListenerAdapter {

    private final MessageUtilService messageUtilService;

    @Autowired
    public ButtonListener(MessageUtilService messageUtilService) {
        this.messageUtilService = messageUtilService;
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        final var ID = event.getButton().getId();
        final var MEMBER = event.getMember();
        if ("ok".equals(ID)) event.getInteraction().getMessage().delete().queue();
        else if ("verify-button".equals(ID)) {
            final var VERIFIED_ROLE = Objects.requireNonNull(event.getGuild(), "Cannot find verified role")
                    .getRolesByName("verified", false)
                    .stream()
                    .findAny();
            if (VERIFIED_ROLE.isPresent()) {
                log.info("verified role is present");
                assert MEMBER != null;
                var isVerified = MEMBER.getRoles()
                        .stream()
                        .map(Role::getName)
                        .anyMatch(e -> VERIFIED_ROLE.get().getName().equals(e));
                messageUtilService.getMessageBuilder().clear();

                if (isVerified) {
                    log.info("User {} is already verified", MEMBER.getUser().getAsTag());
                    messageUtilService.generateAutoDeleteMessage(
                            event,
                            RED,
                            "Already Verified ⛔",
                            "You are already verified"
                    );
                } else {
                    log.info("User {} roles are", MEMBER.getRoles());
                    messageUtilService.generateAutoDeleteMessage(
                            event,
                            RED,
                            "Verified ✅",
                            "You are now verified"
                    );
                    event.getGuild().addRoleToMember(MEMBER, VERIFIED_ROLE.get()).queue();
                    log.info("User {} is verified", MEMBER.getUser().getAsTag());
                }
            } else {
                log.error("Verified role is not present");
                messageUtilService.generateAutoDeleteMessage(
                        event,
                        RED,
                        "Verified Role not found ⛔",
                        "Cannot add role to user\nPlease contact the server admin"
                );
            }
            messageUtilService.getMessageBuilder().clear();
            event.replyEmbeds(messageUtilService.getEmbedBuilder().build())
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal().queueAfter(messageUtilService.getReplyDeletionDelayInMinutes(), MINUTES));
        }
    }
}
