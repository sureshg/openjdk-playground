package dev.suresh.generics;

public class Manager extends Employee {

  public Manager(String name) {
    super(name);
  }

  @Override
  public String toString() {
    return """
        Manager {
          name='%s'
        }
        """.formatted(getName());
  }
}
