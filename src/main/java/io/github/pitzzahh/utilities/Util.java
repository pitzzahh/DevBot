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

import static io.github.pitzzahh.utilities.Print.println;
import net.dv8tion.jda.api.MessageBuilder;
import java.nio.charset.StandardCharsets;
import org.jetbrains.annotations.NotNull;
import net.dv8tion.jda.api.EmbedBuilder;
import com.google.common.io.Resources;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Util {

    private static List<String> notAllowed = Collections.emptyList();

    private static final Map<String, Integer> VIOLATION_COUNT = new HashMap<>();

    private static final Map<String, String> QUESTIONS = new HashMap<>(); // CONTAINS USERNAME AND ANSWER

    public static final MessageBuilder MESSAGE_BUILDER = new MessageBuilder();

    public static final EmbedBuilder EMBED_BUILDER = new EmbedBuilder();

    /**
     * Loads the swear words from a list from a GitHub repository and adds the csv file to a
     * {@code List<String>}.
     * @throws IOException if the list is not present.
     */
    public static void loadSwearWords() throws IOException {
        final var URL = new URL("https://raw.githubusercontent.com/pitzzahh/list-of-bad-words/main/list.txt");
        notAllowed = Resources.readLines(URL, StandardCharsets.UTF_8);
    }

    public static Supplier<List<String>> badWords = () -> notAllowed;

    /**
     * Adds violation to anyone who says a bad words.
     * @param username the username of the user who violated.
     */
    public static void addViolation(final String username) {
        VIOLATION_COUNT.put(username, getViolationCount(username) + 1);
    }

    @NotNull
    private static Integer getViolationCount(String username) {
        return VIOLATION_COUNT.entrySet()
                .stream()
                .filter(e -> e.getKey().equals(username))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(0);
    }

    public static boolean violatedThreeTimes(String username) {
        var violated = getViolationCount(username) >= 3;
        if (violated) VIOLATION_COUNT.remove(username);
        return violated;
    }

    public static BiConsumer<String, String> addQuestion = QUESTIONS::put;

    public static boolean isTheOneWhoPlays(String username) {
        println(QUESTIONS);
        if (isDone.apply(username)) return false;
        return QUESTIONS.keySet()
                .stream()
                .anyMatch(username::equals);
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
        QUESTIONS.replace(player, null);
        return guess.equals(ANSWER);
    }

    public static Function<String, Boolean> isDone = player -> QUESTIONS.entrySet()
            .stream()
            .filter(e -> e.getKey().equals(player))
            .map(Map.Entry::getValue)
            .anyMatch(Objects::isNull);
}
