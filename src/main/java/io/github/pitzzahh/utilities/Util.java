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
package io.github.pitzzahh.utilities;

import net.dv8tion.jda.api.MessageBuilder;
import org.jetbrains.annotations.Contract;
import java.nio.charset.StandardCharsets;
import net.dv8tion.jda.api.EmbedBuilder;
import com.google.common.io.Resources;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Util {

    private static List<String> badWords = Collections.emptyList();

    private static final Map<String, Integer> VIOLATION_COUNT = new HashMap<>();

    private static final Map<String, String> QUESTIONS = new HashMap<>(); // CONTAINS USERNAME AND ANSWER

    public static final MessageBuilder MESSAGE_BUILDER = new MessageBuilder();

    public static final EmbedBuilder EMBED_BUILDER = new EmbedBuilder();

    /**
     * Loads the swear words from a list from a GitHub repository and adds the csv file to a
     * {@code List<String>}.
     * @throws IOException if the list is not present.
     */
    @Contract(pure = true)
    public static void loadSwearWords() throws IOException {
        URL url = new URL("https://raw.githubusercontent.com/pitzzahh/list-of-bad-words/main/list.txt");
        badWords = Resources.readLines(url, StandardCharsets.UTF_8);
    }

    public static Supplier<List<String>> getBadWords = () -> badWords;

    @Contract(pure = true)
    public static void addViolation(final String username) {
        var violationCount = VIOLATION_COUNT.entrySet()
                .stream()
                .filter(e -> e.getKey().equals(username))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(0);
        VIOLATION_COUNT.put(username, violationCount + 1);
    }

    public static boolean violatedThreeTimes(String username) {
        var violationCount = VIOLATION_COUNT.entrySet()
                .stream()
                .filter(e -> e.getKey().equals(username))
                .map(Map.Entry::getValue)
                .findAny()
                .orElse(0);
        if (violationCount >= 3) {
            VIOLATION_COUNT.remove(username);
            return true;
        }
        return false;
    }

    public static BiConsumer<String, String> addQuestion = QUESTIONS::put;

    public static boolean isTheOneWhoPlays(String username) {
        return QUESTIONS.entrySet()
                .stream()
                .anyMatch(e -> e.getKey().equals(username));
    }

    /**
     * Checks if the user answer is correct.
     * @param player the user that is playing.
     * @param guess the user guess on a question.
     * @return {@code true} if the user guessed the answer.
     */
    public static boolean answer(String player, String guess) {
       final var ANSWER =  QUESTIONS.entrySet()
                .stream()
                .filter(e -> e.getKey().equals(player))
                .map(Map.Entry::getValue)
                .collect(Collectors.joining());
       return ANSWER.equals(guess) && QUESTIONS.entrySet().removeIf(e -> e.getKey().equals(player));
    }
}
