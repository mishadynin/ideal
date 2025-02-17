// Autogenerated from runtime/resources/resource_util.i

package ideal.runtime.resources;

import ideal.library.elements.*;
import ideal.library.resources.*;
import ideal.runtime.elements.*;

public class resource_util {
  public final static string PATH_SEPARATOR = new base_string("/");
  public final static string ROOT_CATALOG = new base_string("/");
  public final static string CURRENT_CATALOG = new base_string(".");
  public final static string PARENT_CATALOG = new base_string("..");
  public final static string UTF_8 = new base_string("UTF-8");
  public final static string TEXT_HTML = new base_string("text/html");
  public final static string TEXT_PLAIN = new base_string("text/plain");
  public static void copy(final resource_identifier source, final resource_identifier destination) {
    final string content = source.access_string(null).content().get();
    destination.access_string(null).content().set(content);
  }
}
