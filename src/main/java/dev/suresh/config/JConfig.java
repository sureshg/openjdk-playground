package dev.suresh.config;


import java.util.Objects;

public record JConfig(String language, String version) {

  public JConfig() {
    this(null, null);
  }

  public JConfig {
    language = Objects.requireNonNullElse(language, "Java");
    version = Objects.requireNonNullElse(version, System.getProperty("java.version"));
  }
}
