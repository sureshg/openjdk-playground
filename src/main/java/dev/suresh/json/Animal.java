package dev.suresh.json;

import java.util.List;

public abstract class Animal {

  private final String name;

  // protected Animal() {}

  public Animal(String name) {
    this.name = name;
  }

  public String name() {
    return name;
  }
}

class Cat extends Animal {

  private final boolean canMeow;

  public Cat(String name) {
    super(name);
    canMeow = true;
  }

  public boolean isCanMeow() {
    return canMeow;
  }

  @Override
  public String toString() {
    return """
        Cat{
          name = %s
          canMeow = %s
        }
        """
        .formatted(name(), canMeow);
  }
}

class Dog extends Animal {

  private final boolean canBark;

  public Dog(String name) {
    super(name);
    canBark = true;
  }

  public boolean isCanBark() {
    return canBark;
  }

  @Override
  public String toString() {
    return """
        Dog{
          name = %s
          canBark = %s
        }
        """
        .formatted(name(), canBark);
  }
}

class Animals {

  private final List<Animal> animals;

  public Animals(List<Animal> animals) {
    this.animals = animals;
  }

  public List<Animal> getAnimals() {
    return animals;
  }
}
