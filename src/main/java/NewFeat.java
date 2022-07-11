import static java.lang.System.out;

import java.util.concurrent.CompletableFuture;

public class NewFeat {

  public static void main(String[] args) throws Exception {
    var future = new CompletableFuture<>();
    out.println(future);
    future.complete("hello");
    out.println(future.get());
    out.println(future);
    out.println("Hello");
  }
}

interface MyLister {

  int a = 100;

  String name();

  default String defaname() {
    return "hello";
  }
}

class A implements MyLister {

  @Override
  public String name() {
    return null;
  }

  @Override
  public String defaname() {
    return "dfdfd";
  }
}
