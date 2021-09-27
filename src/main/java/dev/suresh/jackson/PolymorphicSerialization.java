package dev.suresh.jackson;


import java.util.List;

// https://www.mkammerer.de/blog/jacksons-polymorphic-deserialization/
public class PolymorphicSerialization {

  public static void main(String[] args) {

    var cat = new Cat("The cat");
    var dog = new Dog("The dog");
    var animals = List.of(cat, dog);

    System.out.println(animals);

    // new ObjectMapper().

  }
}
