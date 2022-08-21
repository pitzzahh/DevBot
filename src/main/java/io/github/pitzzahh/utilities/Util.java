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

import com.google.common.io.Resources;
import org.jetbrains.annotations.Contract;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class Util {

    private static List<String> badWords = Collections.emptyList();

    private static final Map<String, Integer> VIOLATION_COUNT = new HashMap<>();

    @Contract(pure = true)
    public static void loadSwearWords() throws IOException {
        URL url = new URL("https://raw.githubusercontent.com/pitzzahh/list-of-profanity-words/main/list.txt");
        badWords = Resources.readLines(url, StandardCharsets.UTF_8);
    }

    public static Supplier<List<String>> getBadWords = () -> badWords;

    @Contract(pure = true)
    public static void addViolation(final String username) {
        var violationCount = Util.VIOLATION_COUNT.entrySet()
                .stream()
                .filter(e -> e.getKey().equals(username))
                .map(Map.Entry::getValue)
                .findAny()
                .orElse(0);
        Util.VIOLATION_COUNT.put(username, violationCount + 1);
    }

    public static boolean violatedThreeTimes(String username) {
        var violationCount = Util.VIOLATION_COUNT.entrySet()
                .stream()
                .filter(e -> e.getKey().equals(username))
                .map(Map.Entry::getValue)
                .findAny()
                .orElse(0);
        if (violationCount >= 3) {
            Util.VIOLATION_COUNT.remove(username);
            return true;
        }
        return false;
    }
}
