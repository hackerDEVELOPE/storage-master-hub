package org.example.stream.test;

public class Person {
    String name;

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void printName() {
        System.out.println(this.getName());;
    }

    @Override
    public String toString() {
        return name;
    }
}
