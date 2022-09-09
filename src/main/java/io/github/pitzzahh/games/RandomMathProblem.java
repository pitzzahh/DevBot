package io.github.pitzzahh.games;

import io.github.pitzzahh.games.enums.Difficulty;
import com.github.pitzzahh.computing.Calculator;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import java.math.BigDecimal;
import java.util.Random;

public class RandomMathProblem {

    private static final Calculator<Number, Number, Number> calculator = new Calculator<>();
    private static final Random RANDOM = new Random();
    private static int range;
    private static int firstNumber;
    private static int secondNumber;
    private static int operation;
    private static String toBeAnswered;

    /**
     * Plays a random math.
     * @throws IllegalAccessException call to getCalculation() is redundant.
     */
    public static void play() throws IllegalAccessException {
        firstNumber = RANDOM.nextInt(range) + 1;
        secondNumber = RANDOM.nextInt(range) + 1;
        operation = RANDOM.nextInt(4) + 1;
        calculator.setNumbers(firstNumber, secondNumber);
        toBeAnswered = calculator
                .calculate(operation)
                .getCalculation()
                .toString();
    }

    /**
     * Sets the range of operands.
     * @param difficulty the difficulty of the game.
     */
    public static void setDifficulty(Difficulty difficulty) {
        range = switch (difficulty) {
            case EASY -> RANDOM.nextInt(5) + 1;
            case MEDIUM -> RANDOM.nextInt(10) + 5;
            case HARD -> RANDOM.nextInt(20) + 10;
        };
    }

    @Contract(value = " -> new", pure = true)
    public static @NotNull Number getAnswer() {
        return new BigDecimal(toBeAnswered);
    }

    /**
     * Gets the first number.
     * @return the first number
     */
    @Contract(pure = true)
    public static int getFirstNumber() {
        return firstNumber;
    }

    /**
     * Gets the second number.
     * @return the second number
     */
    @Contract(pure = true)
    public static int getSecondNumber() {
        return secondNumber;
    }

    /**
     * Gets the operation used.
     * @return the first number
     */
    @Contract(pure = true)
    public static String getOperation() {
        return switch (operation) {
            case Calculator.MULTIPLY -> "*";
            case Calculator.DIVIDE -> "/";
            case Calculator.ADD -> "+";
            case Calculator.SUBTRACT -> "-";
            default -> throw new IllegalStateException("Unexpected value: " + operation);
        };
    }
}
