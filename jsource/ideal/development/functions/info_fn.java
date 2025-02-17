/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.functions;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.library.texts.*;
import ideal.library.reflections.*;
import ideal.library.messages.*;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.values.*;
import ideal.development.analyzers.*;
import ideal.development.flavors.*;
import ideal.development.notifications.*;

public class info_fn extends base_procedure {

  public info_fn(action_name the_name) {
    super(the_name, procedure_util.make_varags_procedure_type(false,
        library().immutable_void_type(), string_helper.readonly_stringable()));
  }

  @Override
  public entity_wrapper execute(readonly_list<entity_wrapper> args,
      execution_context context) {

    StringBuilder out = new StringBuilder();
    for (int i = 0; i < args.size(); ++i) {
      entity_wrapper arg = args.get(i);
      string as_string = string_helper.to_string(arg, context);
      out.append(utilities.s(as_string));
    }

    string line = new base_string(out.toString());
    log_message the_message = new simple_message(log_level.INFORMATIONAL, line);
    log.log_output.write(the_message);

    return library().void_instance();
  }
}
