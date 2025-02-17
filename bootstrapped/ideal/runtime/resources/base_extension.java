// Autogenerated from runtime/resources/base_extension.i

package ideal.runtime.resources;

import ideal.library.elements.*;
import ideal.library.resources.*;
import ideal.runtime.elements.*;

public enum base_extension implements extension {
  HTML(new base_string("html")),
  IDEAL_SOURCE(new base_string("i")),
  CSS(new base_string("css")),
  TEXT(new base_string("txt")),
  JAVA_SOURCE(new base_string("java")),
  JAVASCRIPT_SOURCE(new base_string("js"));
  private final string the_dot_name;
  private base_extension(final string name) {
    the_dot_name = new base_string(new base_string("."), name);
  }
  public @Override string dot_name() {
    return the_dot_name;
  }
  public @Override string to_string() {
    return the_dot_name;
  }
}
