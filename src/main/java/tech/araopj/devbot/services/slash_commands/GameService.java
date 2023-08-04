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

package tech.araopj.devbot.services.slash_commands;

import tech.araopj.devbot.configs.slash_commands.GameConfig;
import tech.araopj.devbot.games.RandomMathProblemGenerator;
import org.springframework.stereotype.Service;
import java.util.function.BiConsumer;
import lombok.extern.slf4j.Slf4j;
import java.util.Objects;
import java.util.Map;

@Slf4j
@Service
public record GameService(GameConfig gameConfig) {

    public BiConsumer<String, String> addQuestion() {
        return (username, answer) -> {
            log.info("player = " + username);
            log.info("question = " + RandomMathProblemGenerator.getQuestion());
            log.info("answer = " + answer);
            gameConfig.questions().put(username, answer);
        };
    }

    public boolean isTheOneWhoPlays(String username) {
        if (isDone(username)) return false;
        return gameConfig.questions()
                .keySet()
                .stream()
                .anyMatch(username::equals);
    }

    /**
     * Checks if the user answer is correct.
     * param player the user that is playing.
     * param guess the user guess on a question.
     * returns {@code true} if the user guessed the answer.
     */
    public boolean processAnswer(String guess) {
        boolean isCorrectGuess = RandomMathProblemGenerator.isCorrect(guess);
        log.info("isCorrectGuess = " + isCorrectGuess);
        return isCorrectGuess;
    }

    public boolean isDone(String player) {
        return gameConfig
                .questions()
                .entrySet()
                .stream()
                .filter(e -> e.getKey().equals(player))
                .map(Map.Entry::getValue)
                .anyMatch(Objects::isNull);
    }

    public String getAnswer(String player) {
        return gameConfig
                .questions()
                .entrySet()
                .stream()
                .filter(e -> e.getKey().equals(player))
                .map(Map.Entry::getValue)
                .findAny()
                .orElseThrow();
    }
}
