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

package tech.araopj.springpitzzahhbot.commands.slash_commands.commands;

import tech.araopj.springpitzzahhbot.services.slash_commands.GameService;
import static tech.araopj.springpitzzahhbot.games.RandomMathProblemGenerator.*;
import tech.araopj.springpitzzahhbot.services.MessageUtilService;
import tech.araopj.springpitzzahhbot.commands.slash_commands.CommandContext;
import tech.araopj.springpitzzahhbot.commands.slash_commands.SlashCommand;
import tech.araopj.springpitzzahhbot.games.RandomMathProblemGenerator;
import io.github.pitzzahh.util.utilities.classes.enums.Difficulty;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Service;
import static java.util.Objects.requireNonNull;
import static java.lang.String.format;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import static java.awt.Color.*;

@Slf4j
@Service
public record Play(
        MessageUtilService messageUtilService,
        GameService gameService
) implements SlashCommand {

    /**
     * Executes the command.
     *
     * @return a {@code Supplier<CommandContext>}
     */
    @Override
    public Consumer<CommandContext> execute() {
        return this::process;
    }

    /**
     * Contains the process to be executed.
     *
     * @param context the command context containing the information about the command.
     */
    private void process(CommandContext context) {
        final var PLAYER = requireNonNull(context.event().getMember(), "Null player").getEffectiveName();
        final var SELECTED_DIFFICULTY = requireNonNull(context.getEvent().getOption("difficulty"), "Null game difficulty").getAsString();
        final var DIFFICULTY = Difficulty.valueOf(SELECTED_DIFFICULTY);
        final var COLOR = switch (DIFFICULTY) {
            case EASY -> GREEN;
            case MEDIUM -> YELLOW;
            case HARD -> RED;
        };
        setDifficulty(DIFFICULTY);
        log.info("DIFFICULTY = " + RandomMathProblemGenerator.getDifficulty());
        play();
        messageUtilService.getEmbedBuilder()
                .clear()
                .clearFields()
                .setColor(COLOR)
                .setTitle(format("Difficulty: %s", DIFFICULTY.name()))
                .setDescription(RandomMathProblemGenerator.getQuestion());
        context.getEvent()
                .getInteraction()
                .replyEmbeds(messageUtilService.getEmbedBuilder().build())
                .queue();
        gameService.addQuestion().accept(PLAYER, getAnswer());
    }

    /**
     * Gets the name of the command.
     *
     * @return a {@code Supplier<String>}
     */
    @Override
    public Supplier<String> name() {
        return () -> "play";
    }

    /**
     * Gets the command data.
     *
     * @return a {@code Supplier<CommandData>}.
     */
    @Override
    public Supplier<CommandData> getCommandData() {
        return () -> new CommandDataImpl(
                name().get(),
                description().get())
                .addOptions(
                        new OptionData(OptionType.STRING, "game", "Choose your game", true)
                                .setDescription("Select your desired game")
                                .addChoice("Random Math Problem", "RandomMathProblemGenerator")
                                .addChoice("Rock Paper Scissors", "RockPaperScissors")
                                .addChoice("Tic Tac Toe", "TicTacToe"),
                        new OptionData(OptionType.STRING, "difficulty", "The difficulty of the game", true)
                                .setDescription("Select your desired difficulty")
                                .addChoice("EASY", "EASY")
                                .addChoice("MEDIUM", "MEDIUM")
                                .addChoice("HARD", "HARD")
                );
    }

    /**
     * Returns the description of the command.
     *
     * @return a {@code Supplier<String>}.
     */
    @Override
    public Supplier<String> description() {
        return () -> "Play a game";
    }

}
