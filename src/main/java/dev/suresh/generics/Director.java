package dev.suresh.generics;

public class Director extends Manager {

  public Director(String name) {
    super(name);
  }

  @Override
  public String toString() {
    return """
        Director {
          name='%s'
        }
        """.formatted(getName());
  }
}
