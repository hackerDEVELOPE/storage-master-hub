package org.example.stream;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class StreamInterfaces {
    public static void main(String[] args) {
        // forEach, peek
        Consumer<String> printer = System.out::println;
        printer.accept("hello");

        // filet, anyMatch. allMatch, noneMatch
        Predicate<Integer> isOdd = integer -> integer % 2 != 0;
        System.out.println(isOdd.test(13));

        // map, flatMap
        Function<String, Integer> length = String::length;
        System.out.println(length.apply("sdsdsd"));

        // collect
        Supplier<Integer> supplier = () -> 1;
        Supplier<Map<String, Map<Integer, Set<Long>>>> mapSupplier = HashMap::new;
    }
}
