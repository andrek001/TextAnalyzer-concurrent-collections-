package org.example;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
   public static BlockingQueue<String> queue1 = new ArrayBlockingQueue<>(100);//очередь для обработки символа 'a'
    public static BlockingQueue<String> queue2 = new ArrayBlockingQueue<>(100);//очередь для обработки символа 'b'
    public static BlockingQueue<String> queue3 = new ArrayBlockingQueue<>(100);//очередь для обработки символа 'c'
    public static Thread textGenerator;
    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
    public static int findMaxCharNumber (BlockingQueue<String> queue, char letter) {
        int number = 0;
        int max = 0;
        String text;
        try {
            while (textGenerator.isAlive()) {
                text = queue.take();
                for (char c: text.toCharArray()) {
                    if (c == letter) {
                        number++;
                    }
                }
                if (number > max) {
                    max = number;
                }
            }
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " was interrupted.");
            return 130;
        }
        return max;
    }
    public static Thread getThread (BlockingQueue<String> queue, char letter) {
        return new Thread( () -> {
            int max = findMaxCharNumber(queue, letter);
            System.out.println("Максимальное количество символов " + letter + " во всех сгенерированных текстах - " + max);
        });
    }

    public static void main(String[] args) throws InterruptedException {
        textGenerator = new Thread(() -> {
           for (int i = 0; i < 10_000; i++) {
               String text = generateText("abc", 100_000);
               try {
                   queue1.put(text);
                   queue2.put(text);
                   queue3.put(text);
               } catch (InterruptedException e) {
                   throw new RuntimeException(e);
               }
           }
        });
        textGenerator.start();
        Thread.sleep(10_000); //ждем генерации всех текстов
        Thread a = getThread(queue1, 'a');
        Thread b = getThread(queue2, 'b');
        Thread c = getThread(queue3, 'c');
        a.start();
        b.start();
        c.start();
        a.join();
        b.join();
        c.join();

    }
}