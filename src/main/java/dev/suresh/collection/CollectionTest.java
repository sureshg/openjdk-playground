package dev.suresh.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class CollectionTest {

  public static void main(String[] args) {

    // MethodHandles.lookup().
    var arr = new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    Arrays.parallelSetAll(arr, i -> arr[i] * 5);

    var start = System.currentTimeMillis();
    var list =
        IntStream.range(1, 100_000)
            .parallel()
            .map(i -> i * 2)
            .collect(
                (Supplier<ArrayList<Integer>>) ArrayList::new, ArrayList::add, ArrayList::addAll);

    var end = System.currentTimeMillis();

    System.out.println("Processing took " + (end - start) + " ms.");
    System.out.println("Final list size " + list.size());

    start = System.currentTimeMillis();
    var longList = LongStream.rangeClosed(1, 100_000).parallel().map(a -> a * 5).boxed().toList();
    end = System.currentTimeMillis();
    System.out.println("Processing took " + (end - start) + " ms.");
    System.out.println("Final list size " + longList.size());

    // new BitSet(64).set(1,true);
    // Stream.generate()
    // Objects
    // Arrays
    // Collections.
    // Threads.virtualThreads().count()
  }
}
