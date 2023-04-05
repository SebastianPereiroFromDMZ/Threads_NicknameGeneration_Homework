import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    private static final String patternString = "abc";
    public static AtomicInteger atomicIntAmazingThreeCharacterNickname = new AtomicInteger(0);
    public static AtomicInteger atomicIntAmazingFourCharacterNickname = new AtomicInteger(0);
    public static AtomicInteger atomicIntAmazingFiveCharacterNickname = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {

        Random random = new Random();
        String[] texts = new String[100_000];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("abc", 3 + random.nextInt(3));
        }

        List<Thread> threadList = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(4);

        Runnable palindromeThread = new Thread(() -> {
            Arrays.stream(texts)
                    .forEach(str -> {
                        int i = 0;
                        boolean hasMatch = true;
                        while (i < (str.length() - 1 - i)) {
                            if (str.charAt(i) != str.charAt(str.length() - 1 - i)) {
                                hasMatch = false;
                                break;
                            }
                            i++;
                        }
                        if (hasMatch) {
                            appendToCounter(str);
                        }
                    });
        });
        executor.execute(palindromeThread);


        Runnable sameLetterThread = new Thread(() -> {
            Arrays.stream(texts)
                    .forEach(str -> {
                        char letter = str.charAt(0);
                        boolean hasMatch = true;
                        for (int i = 0; i < str.length(); i++) {
                            if (str.charAt(i) != letter) {
                                hasMatch = false;
                                break;
                            }
                        }
                        if (hasMatch) {
                            appendToCounter(str);
                        }
                    });
        });
        executor.execute(sameLetterThread);

        Runnable increasingLetterThread = new Thread(() -> {
            Arrays.stream(texts)
                    .forEach(str -> {
                        boolean hasMatch = true;
                        for (int i = 0; i < str.length() - 1; i++) {
                            int letterNumber = patternString.indexOf(str.charAt(i));
                            if (letterNumber > patternString.indexOf(str.charAt(i + 1))) {
                                hasMatch = false;
                                break;
                            }
                        }
                        if (hasMatch) {
                            appendToCounter(str);
                        }
                    });
        });
        executor.execute(increasingLetterThread);

        executor.shutdown();

        System.out.println("Красивых слов с длиной 3: " + atomicIntAmazingThreeCharacterNickname + " шт");
        System.out.println("Красивых слов с длиной 4: " + atomicIntAmazingFourCharacterNickname + " шт");
        System.out.println("Красивых слов с длиной 5: " + atomicIntAmazingFiveCharacterNickname + " шт");

    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    private static void appendToCounter(String str) {
        if (str.length() == 3) {
            atomicIntAmazingThreeCharacterNickname.addAndGet(1);
        } else if (str.length() == 4) {
            atomicIntAmazingFourCharacterNickname.addAndGet(1);
        } else {
            atomicIntAmazingFiveCharacterNickname.addAndGet(1);
        }
    }
}
