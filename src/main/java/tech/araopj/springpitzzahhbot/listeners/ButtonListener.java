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

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.araopj.springpitzzahhbot.utilities.MessageUtil;
import java.util.Objects;
import static java.awt.Color.BLUE;
import static java.awt.Color.RED;
import static java.lang.String.format;
import static java.time.Clock.systemDefaultZone;
import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofLocalizedTime;
import static java.time.format.FormatStyle.SHORT;

@Slf4j
@Component
public class ButtonListener extends ListenerAdapter {

    private final MessageUtil messageUtil;

    @Autowired
    public ButtonListener(MessageUtil messageUtil) {
        this.messageUtil = messageUtil;
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        final var ID = event.getButton().getId();
        final var MEMBER = event.getMember();
        if ("ok".equals(ID)) event.getInteraction().getMessage().delete().queue();
        else if ("verify-button".equals(ID)) {
            final var VERIFIED_ROLE = Objects.requireNonNull(event.getGuild(), "Cannot find verified role").getRolesByName("verified", false).stream().findAny();
            if (VERIFIED_ROLE.isPresent()) {
                log.info("Verified role is present");
                assert MEMBER != null;
                var isVerified = MEMBER.getRoles()
                        .stream()
                        .map(Role::getName)
                        .anyMatch(e -> VERIFIED_ROLE.get().getName().equals(e));
                messageUtil.getMessageBuilder().clear();
                if (isVerified) {
                    log.info("User {} is already verified", MEMBER.getUser().getAsTag());
                    message(false);
                }
                else {
                    log.info("User {} roles are", MEMBER.getRoles());
                    message(true);
                    event.getGuild().addRoleToMember(MEMBER, VERIFIED_ROLE.get()).queue();
                    log.info("User {} is verified", MEMBER.getUser().getAsTag());
                }
                event.getInteraction()
                        .replyEmbeds(messageUtil.getEmbedBuilder().build())
                        .setEphemeral(true)
                        .queue();
            } else log.error("Verified role is not present");
        }
    }

    private void message(boolean flag) {
        log.info("Message is being sent");
        messageUtil.getEmbedBuilder()
                .clear()
                .clearFields()
                .setColor(flag ? BLUE : RED)
                .setTitle(flag ? "Verified ✅" : "Already Verified")
                .setFooter(
                        format(
                                "This message will be automatically deleted on %s",
                                now(systemDefaultZone())
                                        .plusMinutes(1)
                                        .format(ofLocalizedTime(SHORT))
                        )
                );
    }
}
