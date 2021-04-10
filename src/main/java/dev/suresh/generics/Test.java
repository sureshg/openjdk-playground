package dev.suresh.generics;

import static java.lang.System.out;

import java.util.List;

public class Test {

  public static void main(String[] args) {

    var emp = new Employee("Employee-1");
    out.println(emp);

    var mgr = new Manager("Manager-1");
    out.println(mgr);

    var dir = new Director("Director-1");
    out.println(dir);

    var empList = List.of(emp);
    var mgrList = List.of(mgr);
    var dirList = List.of(dir);

    printDetails(empList);
    printDetails(mgrList);
    printDetails(dirList);

    //    LongAdder
    //    test(dirList);
    //    test(mgrList);
    test(empList);
  }

  private static void printDetails(Object o) {
    out.println(o.hashCode());
    out.println(System.identityHashCode(o));
    out.println(o.getClass());
    out.println("-----------");
  }

  private static void test(List<Employee> list) {
    list.add(new Director("hhh"));
  }
}
