package org.kapil.concurrency.sparkjava;

import static spark.Spark.*;

public class HelloWorldService {

  public static void main(String[] args) {
    get("/hello", (req, res) -> "Hello, world");
    get("/hello/:name", (req,res)->
      "Hello, "+ req.params(":name")
    );
  }
}
