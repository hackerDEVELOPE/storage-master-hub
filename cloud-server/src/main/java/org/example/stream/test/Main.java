package org.example.stream.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
//    filterList(List.of(1, 2, "a", "b", "aasf", "1", "123", 123));
//        System.out.println(avg(1, 2, 7));

    }
    static List<String> toUpperCaseList(List<String> list){
        return list.stream()
                .map(String::toUpperCase)
                .toList();
    }




    public static List<Object> filterList(final List<Object> list) {
       return list.stream()
                .filter(o -> o instanceof Integer)
                .toList();

    }

    static double avg(int... ints){
        return IntStream.of(ints)
                .average()
                .orElse(0.0);
    }
}
