package com.coderscampus.assignment;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class Assignment8Tester {

    ConcurrentHashMap<Integer, AtomicInteger> numberCounts = new ConcurrentHashMap<>();

    @Test
    public void test() throws InterruptedException {
        Assignment8 assignment = new Assignment8();

        //created executor service to compute faster
        ExecutorService executorService = Executors.newCachedThreadPool();

        List<CompletableFuture<List<Integer>>> numbersLists = new ArrayList<>();

        //calls getNumbers using executorService and assigns to completable future variable. Then adds the completable future to a list.
        for (int i = 0; i < 1000; i++) {
            CompletableFuture<List<Integer>> completableFuture = CompletableFuture.supplyAsync(assignment::getNumbers, executorService);

            numbersLists.add(completableFuture);
        }

        //loops until the all of the completable futures are complete
        while (numbersLists.stream()
                           .filter(CompletableFuture::isDone)
                           .count() < 1000) {
            //print
            System.out.println("Completed Threads: " + numbersLists.stream()
                                                                   .filter(CompletableFuture::isDone)
                                                                   .count());
        }

        //Thread.sleep(1000);
        /*
         * Question - would you prefer the while loop version of waiting for the threads to finish,
         * or the Thread.sleep(1000) version? Thanks.
         */

        //checks if completable futures are all complete
        if(numbersLists.stream()
                       .filter(CompletableFuture::isDone)
                       .count() == 1000)
        {
            //streams through numbers and gets count of each unique number and adds it into a concurrent hash map
            numbersLists.stream().forEach(listCompletableFuture -> {
                try {
                    listCompletableFuture.get().stream().forEach(number -> numberCounts.computeIfAbsent(number, k -> new AtomicInteger(0)).incrementAndGet());
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            });

            //prints the concurrent hash map values
            synchronized (numberCounts)
            {
                numberCounts.forEach((k,v) -> System.out.println(k + " = " + v));
            }

        }

    }

}
