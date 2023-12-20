package dev.suresh.collection;

import static java.lang.System.out;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class CollectionTest {

  void main() {

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

    out.println(STR."Processing took \{end - start} ms.");
    out.println(STR."Final list size \{list.size()}");

    start = System.currentTimeMillis();
    var longList = LongStream.rangeClosed(1, 100_000).parallel().map(a -> a * 5).boxed().toList();
    end = System.currentTimeMillis();
    out.println(STR."Processing took \{end - start} ms.");
    out.println(STR."Final list size \{longList.size()}");

    // new BitSet(64).set(1,true);
    // Stream.generate()
    // Objects
    // Arrays
    // Collections.
    // Threads.virtualThreads().count()
  }

  public static void teeing() {
    record Employee(String name, int age) {}

    BiFunction<String, Integer, Employee> empRef = Employee::new;
    var emp = empRef.apply("name", 10);
    out.println(emp);

    var employees =
        Stream.of(
            new Employee("Employee-1", 30),
            new Employee("Employee-2", 40),
            new Employee("Employee-3", 34),
            new Employee("Employee-4", 45),
            new Employee("Employee-5", 50));

    // employees.collect(Collectors.teeing()

  }
}
