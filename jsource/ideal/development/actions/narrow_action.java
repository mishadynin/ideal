/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.actions;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.notifications.*;
import ideal.development.types.*;
import ideal.development.values.*;
import ideal.development.flavors.*;
import ideal.development.declarations.*;

public class narrow_action extends base_action {
  public final action expression;
  public final type the_type;
  public final variable_declaration the_declaration;

  public narrow_action(action expression, type the_type, variable_declaration the_declaration,
      position source) {
    super(source);
    this.expression = expression;
    this.the_type = the_type;
    this.the_declaration = the_declaration;
    // TODO: just remove the_declaration parameter.
    assert expression.get_declaration() == the_declaration;
    assert the_type.is_subtype_of(
        common_library.get_instance().value_type().get_flavored(flavors.any_flavor));
  }

  @Override
  public abstract_value result() {
    return the_type;
  }

  @Override
  public declaration get_declaration() {
    return the_declaration;
  }

  @Override
  public entity_wrapper execute(execution_context the_context) {
    entity_wrapper expression_result = expression.execute(the_context);
    value_wrapper value_result;
    if (expression_result instanceof readonly_reference_wrapper) {
      value_result = ((readonly_reference_wrapper) expression_result).get();
    } else {
      assert expression_result instanceof value_wrapper;
      value_result = (value_wrapper) expression_result;
    }

    if (!action_utilities.is_of(value_result, the_type)) {
      utilities.panic("Narrow must always succeed.");
    }

    return value_result;
  }

  @Override
  public string to_string() {
    return new base_string("narrow " + expression + " to " + the_type);
  }
}
