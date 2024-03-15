package org.example.netty.playing;

import java.io.FileInputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        executorService.execute(() -> System.out.println("HIXXXXXXXXXXXXXXX1"));
        executorService.execute(() -> System.out.println("HIXXXXXXXXXXXXXXX2"));
        executorService.execute(() -> System.out.println("HIXXXXXXXXXXXXXXX3"));

        Future<String> callableTask = executorService.submit(() -> {
            System.out.println("Callable task");
            return "value";
        });
//        callableTask.get();
//        callableTask.isDone();

        executorService.shutdown();
    }

}
