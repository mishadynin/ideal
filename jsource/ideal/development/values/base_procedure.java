/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.values;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.notifications.*;
import ideal.development.types.*;
import ideal.development.flavors.*;

public abstract class base_procedure extends base_data_value<procedure_value>
    implements procedure_value<procedure_value> {

  private final action_name name;

  public base_procedure(action_name name, type procedure_type) {
    super(procedure_type);
    this.name = name;
  }

  protected static common_library library() {
    return common_library.get_instance();
  }

  @Override
  public action_name name() {
    return name;
  }

  @Override
  public boolean has_this_argument() {
    return false;
  }

  @Override
  public procedure_value bind_from(action from, position pos) {
    if (has_this_argument()) {
      return new procedure_with_this(this, from);
    } else {
      return this;
    }
  }

  protected final abstract_value return_value() {
    return action_utilities.get_procedure_return(type_bound());
  }

  protected final type get_argument_type(int index) {
    return action_utilities.get_procedure_argument(type_bound(), index).type_bound();
  }

  @Override
  public analysis_result bind_parameters(action_parameters params, analysis_context context,
      position pos) {

    readonly_list<action> aparams = params.params();
    // This should never happen because of type checks done before bind_parameters() is called
    if (!action_utilities.is_valid_procedure_arity(type_bound(), aparams.size())) {
      return new error_signal(new base_string("Arity mismatch"), pos);
    }

    list<action> promoted_params = new base_list<action>();

    for (int i = 0; i < aparams.size(); ++i) {
      action param = aparams.get(i);
      if (param instanceof error_signal) {
        return param;
      }
      type target = get_argument_type(i);
      if (context.can_promote(param.result(), target)) {
        promoted_params.append(context.promote(param, target, pos));
      } else {
        return action_utilities.cant_promote(param.result(), target, context, pos);
      }
    }

    return new bound_procedure(this, return_value(), new action_parameters(promoted_params), pos);
  }

  @Override
  public abstract entity_wrapper execute(readonly_list<entity_wrapper> args,
      execution_context the_execution_context);

  @Override
  public string to_string() {
    return utilities.describe(this, name);
  }
}
