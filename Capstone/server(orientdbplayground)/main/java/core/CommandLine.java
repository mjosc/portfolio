package core;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple command line parsing class derived from, but not identical to the implementation shown
 * in <a href="https://stackoverflow.com/a/26376532">this</a> Stack Overflow answer. Provides a 1:1
 * mapping of named parameters to their string values.
 */
public class CommandLine {

  private static final Map<String, String> params  = new HashMap<>();

  /**
   * Parses command line arguments.
   *
   * @throws IllegalArgumentException if the named argument consists of only '-', no named
   * arguments are present or, multiple values follow each named argument.
   */
  public CommandLine(String[] args) {
    int i = 0;
    while (i < args.length) {
      final String arg = args[i];

      if (arg.charAt(0) == '-') {
        if (arg.length() < 2) {
          throw new IllegalArgumentException("Error at argument " + arg);
        }
        params.put(arg.substring(1), args[++i]);
        i++;
      } else {
          throw new IllegalArgumentException("Illegal commandline parameters.");
      }
    }
  }

  /**
   * Returns the Map of all commandline arguments.
   */
  public static Map<String, String> params() {
    return params;
  }

  /**
   * Returns true if the specified argument exists.
   */
  public static boolean contains(String arg) {
    return params.containsKey(arg);
  }

  /**
   * Returns the value to the specified argument if it exists. Null otherwise.
   */
  public static String get(String key) {
    return params.get(key);
  }

}