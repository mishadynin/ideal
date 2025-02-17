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
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.kinds.*;
import static ideal.development.kinds.type_kinds.*;
import ideal.development.types.*;
import ideal.development.flavors.*;
import ideal.development.notifications.*;
import ideal.development.modifiers.*;
import ideal.development.declarations.*;
import ideal.development.values.*;

public class action_utilities {

  private action_utilities() { }

  static final position no_position = new special_position(new base_string("no-position"));

  public static readonly_list<type> lookup_types(analysis_context context, type from,
      action_name name) {
    // TODO: use map.
    list<type> result = new base_list<type>();
    readonly_list<action> types = context.lookup(from, name);
    for (int i = 0; i < types.size(); ++i) {
      action a = types.get(i);
      if (a instanceof type_action) {
        result.append(((type_action) a).get_type());
      } else {
        // This is unexpected; but we should be able just to ignore it.
        // For now, panic to get attention.
        utilities.panic("Type expected " + a);
      }
    }
    return result;
  }

  // TODO: use declaration_utils.get_declared_supertypes()
  public static readonly_list<type> get_supertypes(principal_type the_type) {
    declaration the_declaration = the_type.get_declaration();
    assert the_declaration instanceof type_declaration;
    readonly_list<declaration> signature = ((type_declaration) the_declaration).get_signature();
    list<type> result = new base_list<type>();
    for (int i = 0; i < signature.size(); ++i) {
      declaration d = signature.get(i);
      if (d instanceof supertype_declaration) {
        supertype_declaration the_supertype_declaration = (supertype_declaration) d;
        if (!the_supertype_declaration.has_errors()) {
          result.append(the_supertype_declaration.get_supertype());
        }
      }
    }
    return result;
  }

  public static void add_promotion(analysis_context context, type from, type to, position pos) {
    if (to instanceof principal_type) {
      context.add(from, special_name.PROMOTION, to.to_action(pos));
    } else {
      context.add(from, special_name.PROMOTION, new promotion_action(to, pos));
    }
  }

  public static master_type make_type(analysis_context context, kind kind,
      @Nullable flavor_profile the_flavor_profile, action_name name, principal_type parent,
      @Nullable declaration the_declaration, position pos) {
    master_type result = new master_type(kind, the_flavor_profile, name, parent, context,
        the_declaration);
    context.add(parent, name, result.to_action(pos));
    return result;
  }

  public static action to_value(action expression, position source) {
    type the_type = expression.result().type_bound();
    if (common_library.get_instance().is_reference_type(the_type)) {
      // TODO: check that flavor is readonly or mutable.
      type value_type = common_library.get_instance().get_reference_parameter(the_type);
      // TODO: replace this with a promotion lookup.
      return new dereference_action(value_type, null, source).bind_from(expression, source);
    } else {
      return expression;
    }
  }

  public static boolean is_procedure_type(type the_type) {
    return the_type.principal().get_kind() == type_kinds.procedure_kind;
  }

  public static boolean is_valid_procedure_arity(type procedure_type, int arity) {
    assert is_procedure_type(procedure_type);
    return ((parametrized_type) procedure_type.principal()).get_parameters().
        is_valid_arity(arity + 1);
  }

  public static abstract_value get_procedure_argument(type procedure_type, int index) {
    assert is_procedure_type(procedure_type);
    return ((parametrized_type) procedure_type.principal()).get_parameters().get(index + 1);
  }

  public static abstract_value get_procedure_return(type procedure_type) {
    principal_type the_principal = procedure_type.principal();
    assert the_principal.get_kind() == type_kinds.procedure_kind;

    return ((parametrized_type) the_principal).get_parameters().first();
  }

  public static base_execution_context get_context(execution_context the_context) {
    return (base_execution_context) the_context;
  }

  public static entity_wrapper execute_procedure(procedure_declaration the_procedure,
      @Nullable value_wrapper this_argument, readonly_list<entity_wrapper> arguments,
      execution_context the_context) {

    base_execution_context new_context = action_utilities.get_context(the_context).
        make_child(the_procedure.original_name());

    if (the_procedure.get_category() == procedure_category.STATIC) {
      assert this_argument == null;
    } else {
      assert this_argument != null;
      new_context.put_var(the_procedure.get_this_declaration(), this_argument);
    }

    assert the_procedure.get_argument_types().size() == arguments.size();
    for (int i = 0; i < arguments.size(); ++i) {
      variable_declaration var_decl = the_procedure.get_parameter_variables().get(i);
      entity_wrapper wrapped_argument = arguments.get(i);
      assert wrapped_argument instanceof value_wrapper;
      new_context.put_var(var_decl, (value_wrapper) wrapped_argument);
    }

    action body_action = the_procedure.get_body_action();
    assert body_action != null;
    entity_wrapper result = body_action.execute(new_context);

    // TODO: uniformly hanlde jump_wrappers; do stack trace.
    if (result instanceof panic_value) {
      utilities.panic(((panic_value) result).message);
    }

    if (the_procedure.get_category() == procedure_category.CONSTRUCTOR) {
      result = this_argument;
    } else {
      if (result instanceof returned_value) {
        result = ((returned_value) result).result;
      }
    }
    return result;
  }

  public static boolean is_result(action the_action, abstract_value the_result) {
    // TODO: For now assume this -- but it doesn't *have* to be true.
    assert the_result instanceof type;
    return the_action.result().type_bound().is_subtype_of(the_result.type_bound());
  }

  // Make it easy to detect where type_id needs to be cast to type.
  public static type to_type(type_id the_type_id) {
    return (type) the_type_id;
  }

  public static boolean is_of(entity_wrapper the_entity, type the_type) {
    return to_type(the_entity.type_bound()).is_subtype_of(the_type);
  }

  public static error_signal cant_promote(abstract_value from, type target,
      analysis_context the_context, position pos) {
    return new error_signal(new base_string("Can't promote " + the_context.print_value(from) +
        " to " + the_context.print_value(target)), pos);
  }

  public static flavor_profile get_profile(supertype_declaration the_supertype) {
    type the_type = the_supertype.get_supertype();
    type_utilities.prepare(the_type, declaration_pass.TYPES_AND_PROMOTIONS);
    flavor_profile the_profile = the_type.principal().get_flavor_profile();
    if (the_type.get_flavor() != flavors.nameonly_flavor) {
      the_profile = flavor_profiles.combine(the_profile, the_type.get_flavor().get_profile());
    }
    return the_profile;
  }

  public static boolean supports_constructors(principal_type the_type) {
    kind the_kind = the_type.get_kind();
    return the_kind == class_kind || the_kind == datatype_kind || the_kind == enum_kind;
  }
}
