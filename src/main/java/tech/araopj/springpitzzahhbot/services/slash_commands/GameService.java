package tech.araopj.springpitzzahhbot.services.slash_commands;

import tech.araopj.springpitzzahhbot.configs.slash_commands.GameConfig;
import tech.araopj.springpitzzahhbot.games.RandomMathProblemGenerator;
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
