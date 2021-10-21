package dev.suresh.npe;

public class HelpfulNPE {

  public static void run() {
    try {
      String name = new Customer(new Address(null)).address.country.name;
      System.out.println(name);
    }catch (NullPointerException npe){
      System.out.println("Helpful NPE: " + npe.getMessage());
      assert npe.getMessage().equals("""
        Cannot read field "name" because "address.country" is null""");
    }
  }

  record Customer(Address address) {}

  record Address(Country country) {}

  record Country(String name) {}
}
