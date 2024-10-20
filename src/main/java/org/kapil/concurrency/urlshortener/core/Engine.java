package org.kapil.concurrency.urlshortener.core;

public abstract class Engine {
  public abstract String getUniqueKey(String url);
}
