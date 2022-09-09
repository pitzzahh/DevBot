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

package io.github.pitzzahh.commands.slash_command.commands;

import io.github.pitzzahh.games.enums.Difficulty;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import io.github.pitzzahh.commands.slash_command.CommandContext;
import io.github.pitzzahh.commands.slash_command.SlashCommand;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import io.github.pitzzahh.games.RandomMathProblem;
import static io.github.pitzzahh.utilities.Util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.Objects;
import java.awt.*;

public class Game implements SlashCommand {

    /**
     * Executes the command.
     * @return a {@code Supplier<CommandContext>}
     */
    @Override
    public Consumer<CommandContext> execute() {
        return context -> {
            try {
                final var PLAYER = Objects.requireNonNull(context.event().getMember()).getEffectiveName();
                final var SELECTED_DIFFICULTY = Objects.requireNonNull(context.getEvent().getOption("difficulty")).getAsString();
                final var DIFFICULTY = Difficulty.valueOf(SELECTED_DIFFICULTY);
                System.out.println("DIFFICULTY = " + DIFFICULTY);
                final var COLOR = switch (DIFFICULTY) {
                    case EASY -> Color.GREEN;
                    case MEDIUM -> Color.YELLOW;
                    case HARD -> Color.RED;
                };
                RandomMathProblem.setDifficulty(DIFFICULTY);
                RandomMathProblem.play();
                EMBED_BUILDER.clear()
                        .clearFields()
                        .setColor(COLOR)
                        .setTitle(String.format("Difficulty: %s", DIFFICULTY.name()))
                        .setDescription(
                                String.format(
                                        "Problem: %d %s %d = ?",
                                        RandomMathProblem.getFirstNumber(),
                                        RandomMathProblem.getOperation(),
                                        RandomMathProblem.getSecondNumber()
                                )
                        );

                context.getEvent()
                        .getInteraction()
                        .replyEmbeds(EMBED_BUILDER.build())
                        .queue();
                addQuestion.accept(PLAYER, RandomMathProblem.getAnswer().toString());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * Gets the name of the command.
     * @return a {@code Supplier<String>}
     */
    @Override
    public Supplier<String> name() {
        return () -> "play";
    }

    /**
     * Gets the command data.
     * @return a {@code Supplier<CommandData>}.
     */
    @Override
    public Supplier<CommandData> getCommandData() {
        return () -> new CommandDataImpl(
                name().get(),
                description().get())
                .addOptions(
                        new OptionData(OptionType.STRING, "game", "Choose your game", true)
                                .setDescription("GAMES")
                                .addChoice("Random Math Problem", "RMP"),
                        new OptionData(OptionType.STRING, "difficulty", "The difficulty of the game", true)
                                .setDescription("DIFFICULTY")
                                .addChoice("EASY", "EASY")
                                .addChoice("MEDIUM", "MEDIUM")
                                .addChoice("HARD", "HARD")
        );
    }

    /**
     * Returns the description of the command.
     * @return a {@code Supplier<String>}.
     */
    @Override
    public Supplier<String> description() {
        return () -> "Play a game";
    }

}
