package dev.suresh.jte;

import java.util.Objects;

public record Config(String language, String version) {

  public Config() {
    this(null, null);
  }

  public Config {
    language = Objects.requireNonNullElse(language, "Java");
    version = Objects.requireNonNullElse(version, System.getProperty("java.version"));
  }
}
