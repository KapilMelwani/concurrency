package org.kapil.concurrency.urlshortener.core;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashingEngine extends Engine {

  @Override
  public String getUniqueKey(String url) {
    return MD5(url).substring(0, 7);
  }

  private String MD5(String input) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] digest = md.digest(input.getBytes());
      StringBuilder sb = new StringBuilder();
      for (byte b : digest) sb.append(String.format("%02x", b));
      return sb.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }
}
