package dev.suresh.adt;

import static java.lang.System.out;
import static java.util.Objects.requireNonNull;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.RecordComponent;
import java.nio.file.Files;
import java.util.List;

public class Records {

  public static void main(String[] args) throws Exception{
    run();
  }

  public static void run() throws Exception{
    amberReflections();
    serializeRecord();
  }

  private static void amberReflections() {
    var sealedClazz= Result.class;
    out.println("Result (Interface)  -> " + sealedClazz.isInterface());
    out.println("Result (Sealed Class) -> " + sealedClazz.isSealed());

    for (Class<?> permittedSubclass : sealedClazz.getPermittedSubclasses()) {
      out.println("\nPermitted Subclass : " + permittedSubclass.getName());
      if(permittedSubclass.isRecord()) {
        out.println(permittedSubclass.getSimpleName() + " record components are,");
        for (RecordComponent rc : permittedSubclass.getRecordComponents()) {
          out.println(rc);
        }
      }
    }
  }

  @SuppressWarnings("rawtypes")
  private static void serializeRecord() throws Exception {
    // Local record
    record Lang(String name, int age) implements Serializable {
      Lang {
        requireNonNull(name);
        if(age <= 0) throw new IllegalArgumentException("Invalid age " + age);
      }
    }

    var serialFile = Files.createTempFile("record-serial", "data").toFile();
    serialFile.deleteOnExit();

    try (var oos = new ObjectOutputStream(new FileOutputStream(serialFile))) {
      List<Record> recs = List.of(
          new Lang("Java", 25),
          (Record) Result.success(100)
      );

      for (Record rec : recs) {
        out.println("Serializing record: " +  rec);
        oos.writeObject(rec);
      }
      oos.writeObject(null); //EOF
    }

    try (var ois = new ObjectInputStream(  new FileInputStream(serialFile))) {
      Object rec;
      while((rec = ois.readObject()) != null) {
        out.println("Deserialized record: " + rec);
        if(rec instanceof Lang l) {
          out.println("Lang name: " + l.name);
        }else if(rec instanceof Result r) {
          out.println("Result value: " + r.getOrNull());
        } else {
          System.err.println("Invalid serialized data. Expected Result, but found " + rec);
        }
      }
    }
  }
}
