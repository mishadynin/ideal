// Autogenerated from runtime/channels/output_transformer.i

package ideal.runtime.channels;

import ideal.library.elements.*;
import ideal.library.channels.*;
import ideal.runtime.elements.*;

public class output_transformer<source_type, destination_type> implements output<source_type> {
  private final function1<destination_type, source_type> the_function;
  private final output<destination_type> the_output;
  public output_transformer(final function1<destination_type, source_type> the_function, final output<destination_type> the_output) {
    this.the_function = the_function;
    this.the_output = the_output;
  }
  public @Override void write(final source_type value) {
    the_output.write(the_function.call(value));
  }
  public @Override void write_all(final readonly_list<source_type> values) {
    final base_list<destination_type> transformed_values = new base_list<destination_type>();
    for (int i = 0; i < values.size(); i += 1) {
      transformed_values.append(the_function.call(values.get(i)));
    }
    the_output.write_all(transformed_values);
  }
  public @Override void sync() {
    the_output.sync();
  }
  public @Override void close() {
    the_output.close();
  }
}
