package io.github.pitzzahh;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Arrays;
import java.io.IOException;
import java.util.Collection;
import kotlin.text.Charsets;
import java.util.Collections;
import com.google.common.io.Files;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;
import com.google.common.io.Resources;
import com.github.pitzzahh.utilities.SecurityUtil;

public class Util {

    public static List<String[]> JOKES = Collections.EMPTY_LIST;
    public static List<String> BAD_WORDS = Collections.EMPTY_LIST;

    /**
     * Loads the jokes from the text file and adding it to the {@code List<String[]> JOKES}.
     * @throws IOException if the file that contains the jokes does not exist or invalid.
     */
    public static void loadJokes() throws IOException {
       JOKES = getFileContents(new URL("https://raw.githubusercontent.com/pitzzahh/pitzzahh-bot/main/src/main/resources/jokes.txt"))
               .stream()
               .map(e -> e.split("[?]"))
               .map(e -> new String[]{SecurityUtil.decrypt(e[0]), SecurityUtil.decrypt(e[1])})
               .collect(Collectors.toList());
    }

    /**
     * Loads the bad words from the text file and adding it to the {@code List<String> BAD_WORDS}.
     * @throws IOException if the file that contains the jokes does not exist or invalid.
     */
    public static void loadBadWords() throws IOException {
        BAD_WORDS = getFileContents(new URL("https://raw.githubusercontent.com/pitzzahh/pitzzahh-bot/main/src/main/resources/bad_words.txt"))
                .stream()
                .map(e -> e.split(","))
                .map(Arrays::asList)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * Gets the file contents of each line and stores it to a {@code List<String>}.
     * @param url the url that contains a file, the file contents to read.
     * @return a {@code List<String>} the contents of the file.
     * @throws IOException if something is wrong with file operations.
     * @see Resources
     */
    public static List<String> getFileContents(URL url) throws IOException {
        return Resources.readLines(url, Charsets.UTF_8);
    }

    /**
     * Gets the file contents of each line and stores it to a {@code List<String>}.
     * @param file the file to get the contents.
     * @return a {@code List<String>} the contents of the file.
     * @throws IOException if something is wrong with file operations.
     * @see Resources
     */
    public static List<String> getFileContents(File file) throws IOException {
        return Files.readLines(file, Charsets.UTF_8)
                .stream()
                .collect(Collectors.toList());
    }

    /**
     * Gets a delay based on the length of the joke.
     * @param question the quesiton joke.
     * @return an {@code int} the lenght of the delay.
     */
    public static int getDelay(final String question) {
        var length = question.length();
        if (length >= 10 && length <= 14) return 500;
        else if (length >= 10 && length <= 14) return 1000;
        else if (length >= 15 && length <= 19) return 1500;
        else if (length >= 20 && length <= 24) return 2000;
        else if (length >= 25 && length <= 29) return 2500;
        else if (length >= 30 && length <= 34) return 3000;
        else if (length >= 35 && length <= 39) return 3500;
        else if (length >= 40 && length <= 44) return 4000;
        else if (length >= 45 && length <= 49) return 4500;
        else if (length >= 50 && length <= 60) return 5000;
        else return 6000;
    }

    /**
     * pauses for a given time.
     * @param time the time to sleep in ms.
     */
    public static void sleep(final int time) {
        try {
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
