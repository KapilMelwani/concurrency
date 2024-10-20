package org.kapil.concurrency.urlshortener;

import java.security.NoSuchAlgorithmException;
import org.kapil.concurrency.urlshortener.core.EngineType;

public class UrlShortener {

  private final EngineFactory factory;

  public UrlShortener(EngineFactory factory) {
    this.factory = factory;
  }

  public String shorten(String longUrl, String alias, EngineType type)
      throws NoSuchAlgorithmException {
    String shortKey;
    if (alias == null || alias.isEmpty()) {
      shortKey = factory.getEngine(type, 1, 1).getUniqueKey(longUrl);
    } else {
      shortKey = alias;
    }

    if (DB.cache.putIfAbsent(shortKey, longUrl) != null) {
      throw new IllegalArgumentException("Alias already exists");
    }

    return String.format("%s", shortKey);
  }

  public String resolver(String shortUrl) {
    try {
      String longUrl = DB.cache.get(shortUrl);
      if (longUrl == null || longUrl.isEmpty()) {
        throw new IllegalArgumentException("No such URL exists");
      }
      return longUrl;
    } catch (Exception e) {
      throw new IllegalArgumentException("Malformed URL");
    }
  }
}
