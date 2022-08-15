package io.github.pitzzahh;

import java.io.File;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;
import com.github.pitzzahh.utilities.SecurityUtil;

public class Util {

    public static List<String[]> JOKES = Collections.EMPTY_LIST;

    /**
     * Loads the jokes from the text file and adding it to the {@code List<String[]> JOKES}.
     * @throws IOException if the file that contains the jokes does not exist or invalid.
     */
    public static void loadJokes() throws IOException {
       JOKES = getFileContents(new File("src/main/resources/jokes.txt"), 0)
               .stream()
               .map(e -> e.split("[?]"))
               .map(e -> new String[]{SecurityUtil.decrypt(e[0]), SecurityUtil.decrypt(e[1])})
               .collect(Collectors.toList());
    }

    /**
     * Gets the file contents of each line and stores it to a {@code List<String>}.
     * @param file the file to get the contents
     * @param line what line in the text file to skip, if 0, no lines will be skipped
     * @return a {@code List<String>} the contents of the file.
     * @throws IOException if something is wrong with file operations.
     */
    public static List<String> getFileContents(File file, int line) throws IOException {
        return Files.lines(Paths.get(file.getAbsolutePath()))
                .skip(line)
                .collect(Collectors.toList());
    }
    /**
     * Gets a delay based on the length of the joke.
     * @param question the quesiton joke.
     * @return an {@code int} the lenght of the delay.
     */
    public static int getDelay(final String question) {
        switch (question.length()) {
            case 10,11,12,13,14 -> {
                return 500;
            }
            case 15,16,17,18,19 -> {
                return 1000;
            }
            case 20,21,22,23,24 -> {
                return 1500;
            }
            case 25,26,27,28,29 -> {
                return 2000;
            }
            case 30,31,32,33,34 -> {
                return 2500;
            }
            case 35,36,37,38,39 -> {
                return 3000;
            }
            case 40,41,42,43,44 -> {
                return 3500;
            }
            case 45,46,47,48,49 -> {
                return 4000;
            }
            case 50,51,52,53,54,55,56,57,58,59 -> {
                return 4500;
            }
        }
        return 5000;
    }
}
