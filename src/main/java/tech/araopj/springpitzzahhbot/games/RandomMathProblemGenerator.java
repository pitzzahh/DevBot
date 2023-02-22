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

package tech.araopj.springpitzzahhbot.games;

import static io.github.pitzzahh.util.utilities.classes.enums.Operation.*;
import io.github.pitzzahh.util.utilities.classes.enums.Difficulty;
import io.github.pitzzahh.util.utilities.classes.enums.Operation;
import io.github.pitzzahh.util.computing.Calculator;
import org.springframework.stereotype.Component;
import static java.math.RoundingMode.HALF_UP;
import static java.lang.String.valueOf;
import static java.lang.String.format;
import lombok.extern.slf4j.Slf4j;
import java.math.MathContext;
import java.util.Objects;
import java.util.Random;

/**
 * Class used to play random math problem.
 */
@Slf4j
@Component
public class RandomMathProblemGenerator {

    private static final Calculator calculator = new Calculator();
    private static final Random RANDOM = new Random();
    private static Number firstNumber;
    private static Number secondNumber;
    private static Operation operation;
    private static Difficulty difficulty;
    private static String toBeAnswered;

    /**
     * Plays a random math.
     */
    public static void play() {
        Objects.requireNonNull(difficulty, "Please set a difficulty");
        operation = getRandomOperation();
        log.debug(getQuestion());
        final var RESULT = calculator.calculate(firstNumber, secondNumber, operation);
        toBeAnswered = operation == DIVISION ? valueOf(RESULT.round(new MathContext(2, HALF_UP)) ): valueOf(RESULT);
    }

    /**
     * Sets the range of operands.
     */
    public static void setDifficulty(Difficulty diff) {
        difficulty = diff;
        switch (difficulty) {
            case EASY -> {
                firstNumber = RANDOM.nextInt(5) + 1;
                secondNumber = RANDOM.nextInt(5) + 1;
            }
            case MEDIUM -> {
                firstNumber = RANDOM.nextInt(10) + 5;
                secondNumber = RANDOM.nextInt(10) + 5;
            }
            case HARD -> {
                firstNumber = RANDOM.nextInt(20) + 10;
                secondNumber = RANDOM.nextInt(20) + 10;
            }
        }
    }

    private static Operation getRandomOperation() {
        var randomNumber = RANDOM.nextInt(4) + 1;
        return switch (randomNumber) {
            case 1 -> MULTIPLICATION;
            case 2 -> DIVISION;
            case 3 -> ADDITION;
            case 4 -> SUBTRACTION;
            default -> throw new UnsupportedOperationException("Unsupported operation: " + randomNumber);
        };
    }

    /**
     * Gets the answer on a computation.
     * @return the answer as a {@code String}.
     */
    public static String getAnswer() {
        return toBeAnswered;
    }

    public static boolean isCorrect(String guess) {
        return guess.contentEquals(toBeAnswered);
    }

    /**
     * Gets the first number.
     * @return the first number
     */
    public static Number getFirstNumber() {
        return firstNumber;
    }

    /**
     * Gets the second number.
     * @return the second number
     */
    public static Number getSecondNumber() {
        return secondNumber;
    }

    /**
     * Gets the operation used.
     * @return the first number
     */
    public static String getOperation() {
        return switch (operation) {
            case MULTIPLICATION -> "*";
            case DIVISION -> "/";
            case ADDITION -> "+";
            case SUBTRACTION -> "-";
            case MODULO -> "%";
        };
    }

    public static Difficulty getDifficulty() {
        return difficulty;
    }

    public static String getQuestion() {
        return format("%s %s %s = ?", getFirstNumber(), getOperation(), getSecondNumber());
    }
}