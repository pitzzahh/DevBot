package io.github.pitzzahh;

public class Util {
    public static final String[] JOKES = {
            "Why did the scarecrow win an award? Because he was outstanding in his field.",
            "What do you call a pig that does karate? A pork chop.",
            "Why did the melon jump into the lake? It wanted to be a water-melon.",
            "Why did the drum take a nap? It was beat.",
            "What do you call two monkeys that share an Amazon account? Prime mates.",
            "Why did the tree go to the dentist? It needed a root canal.",
            "Where do cows go for entertainment? The mooooo-vies!",
            "What’s an astronaut’s favorite candy? A Mars bar.",
            "Where do sheep go to get their hair cut? The baa-baa shop.",
            "How do you know when the moon has had enough to eat? When it’s full.",
            "What kind of music do planets like? Neptunes.",
            "How do you tell if a vampire is sick? By how much he is coffin.",
            "What is the difference between a teacher and a train? One says, \"Spit out your gum,\" and the other says, \"Choo choo choo\".",
            "Why does Humpty Dumpty love autumn? Because Humpty Dumpty had a great fall.",
            "How do trees access the internet? They log in.",
            "Why is it sad that parallel lines have so much in common? Because they’ll never meet.",
            "Why are obtuse angles so depressed? Because they’re never right.",
            "Why did the bee get married? He found his honey.",
            "What do you call a fake noodle? An impasta.",
            "What does it make you if you see a robbery at an Apple Store? An iwitness.",
            "What is an astronaut’s favorite key on a keyboard? The space bar.",
            "Can February March? No but April May"
    };

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
