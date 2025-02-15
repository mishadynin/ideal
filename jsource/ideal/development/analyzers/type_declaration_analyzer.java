/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.kinds.*;
import ideal.development.modifiers.*;
import ideal.development.flavors.*;
import ideal.development.declarations.*;

public class type_declaration_analyzer extends declaration_analyzer<type_declaration_construct>
    implements type_declaration {

  private master_type master;
  private base_principal_type result_type;
  private @Nullable principal_type inside_type;
  private @Nullable readonly_list<type_parameter_analyzer> parameters;
  private @Nullable readonly_list<analyzable> body;
  private boolean master_already_declared;
  private @Nullable type_parameters parameter_values;
  private boolean declaration_analysis_in_progress;

  public type_declaration_analyzer(type_declaration_construct source) {
    super(source);
    assert source.body != null;
    assert source.annotations != null;
  }

  public type_declaration_analyzer(type_declaration_construct source,
      principal_type parent, analysis_context context) {
    super(source, parent, context);
    assert source.body != null;
    assert source.annotations != null;

    // TODO: this should be moved out of constructor.
    @Nullable analyzable before = context.get_analyzable(source);
    assert before == null;
    context.put_analyzable(source, this);
  }

  @Override
  public principal_type inner_type() {
    assert inside_type != null;
    return inside_type;
  }

  private readonly_list<type_parameter_analyzer> make_var_list(
      readonly_list<construct> constructs, analysis_pass pass) {
    list<type_parameter_analyzer> result = new base_list<type_parameter_analyzer>();
    for (int i = 0; i < constructs.size(); ++i) {
      construct the_construct = constructs.get(i);
      type_parameter_analyzer type_parameter;
      // TODO: handle errors
      if (the_construct instanceof variable_construct) {
        type_parameter = new type_parameter_analyzer((variable_construct) the_construct);
      } else if (the_construct instanceof name_construct) {
        type_parameter = new type_parameter_analyzer(
            ((name_construct) the_construct).the_name, the_construct);
      } else {
        utilities.panic("Error in type variable");
        return null;
      }
      analyze_and_ignore_errors(type_parameter, pass);
      result.append(type_parameter);
    }

    return result;
  }

  public @Nullable readonly_list<type_parameter_declaration> get_parameters() {
    // Argh.  Java generics strike again.
    return (readonly_list<type_parameter_declaration>) (readonly_list) parameters;
  }

  @Override
  protected boolean static_declaration() {
    return true;
  }

  @Override
  public action_name short_name() {
    return source.name;
  }

  @Override
  public kind get_kind() {
    return source.kind;
  }

  @Override
  public principal_type get_declared_type() {
    assert result_type != null;
    return result_type;
  }

  @Override
  public readonly_list<declaration> get_signature() {
    list<declaration> types = new base_list<declaration>();

    for (int i = 0; i < body.size(); ++i) {
      analyzable a = body.get(i);
      if (a instanceof declaration) {
        types.append((declaration) a);
      }
    }

    return types;
  }

  private boolean has_supertype_declarations() {
    // TODO: use collection.has()
    for (int i = 0; i < body.size(); ++i) {
      analyzable the_analyzable = body.get(i);
      if (the_analyzable instanceof supertype_declaration) {
        return true;
      }
    }

    return false;
  }

  private readonly_list<analyzable> make_body_list(readonly_list<construct> constructs) {
    list<analyzable> body_list = new base_list<analyzable>();
    int enum_value_ordinal = 0;
    for (int i = 0; i < constructs.size(); ++i) {
      construct the_construct = constructs.get(i);
      if (enum_util.can_be_enum_value(the_construct)) {
        if (get_kind() == type_kinds.enum_kind) {
          body_list.append(new enum_value_analyzer(the_construct, enum_value_ordinal));
          enum_value_ordinal += 1;
        } else {
          new base_notification("Value declaration in non-enum type", the_construct).report();
        }
      } else if (the_construct instanceof supertype_construct) {
        // TODO: clean up.
        body_list.append_all(new dispatcher().make_supertype_list(
            (supertype_construct) the_construct, this));
        associate_with_this(the_construct);
      } else {
        body_list.append(make(the_construct));
      }
    }
    return body_list;
  }

  public boolean has_parameters() {
    return source.has_parameters();
  }

  @Override
  protected @Nullable error_signal do_multi_pass_analysis(analysis_pass pass) {

    if (pass == analysis_pass.TARGET_DECL) {
      process_annotations(source.annotations, language().get_default_type_access(outer_kind()));

      readonly_list<action> resolved = get_context().lookup(declared_in_type(), short_name());

      if (resolved.size() > 1) {
        return new error_signal(messages.name_type_used, source);
      }

      master_already_declared = resolved.size() == 1;
      if (master_already_declared) {
        action maybe_type_action = resolved.first();
        if (! (maybe_type_action instanceof type_action)) {
          return new error_signal(messages.type_expected, source);
        }
        type maybe_master = ((type_action) maybe_type_action).get_type();
        if (! (maybe_master instanceof master_type)) {
          // TODO: what error should be here?
          return new error_signal(messages.name_type_used, source);
        }
        master = (master_type) maybe_master;
        if (master.get_kind() != get_kind()) {
          return new error_signal(messages.wrong_kind, source);
        }
        if (master.get_declaration() == null) {
          master.set_declaration(this);
        }
      } else {
        master = action_utilities.make_type(get_context(), get_kind(), null,
            short_name(), declared_in_type(), this, this);
      }

      if (has_parameters()) {
        return null;
      }

      result_type = master;
      inside_type = make_inside_type(result_type, this);
      body = make_body_list(source.body);
    }

    if (pass == analysis_pass.TYPE_DECL) {
      if (has_parameters()) {
        if (master.get_primary() != null) {
          // TODO: support multiple primary types.
          return primary_already_defined();
        }
        parametrized_type with_params = master.make_primary();
        with_params.set_declaration(this);
        result_type = with_params;
        add_promotion(result_type, master);

        inside_type = make_inside_type(result_type, this);
      }


      if (parent() != core_types.root_type()) {
        add_promotion(inside_type, core_types.root_type());
      }

      if (has_parameters()) {
        parameters = make_var_list(source.parameters.elements, pass);
      }

      if (body == null) {
        body = make_body_list(source.body);
      }
    }

    if (body == null) {
      assert has_errors();
      return null;
    }

    if (pass == analysis_pass.IMPORT_AND_TYPE_VAR_DECL && has_parameters()) {
      for (int i = 0; i < parameters.size(); ++i) {
        analyze_and_ignore_errors(parameters.get(i), pass);
      }
    }

    if (pass == analysis_pass.SUPERTYPE_DECL) {
      if (has_parameters()) {
        list<abstract_value> parameter_builder = new base_list<abstract_value>();

        for (int i = 0; i < parameters.size(); ++i) {
          type_parameter_analyzer tvar = parameters.get(i);
          if (has_errors(tvar, pass)) {
            parameter_builder.append(core_types.error_type());
          } else {
            parameter_builder.append(tvar.get_declared_type());
          }
        }

        parameter_values = new type_parameters(parameter_builder);
        @Nullable parametrized_type already_defined = master.lookup_parametrized(parameter_values);
        if (already_defined != null) {
          readonly_list<notification> secondary;
          if (already_defined.get_declaration() != null) {
            secondary = new base_list<notification>(
              new base_notification("First declaration", already_defined.get_declaration()));
          } else {
            secondary = null;
          }
          notification primary_notification = new base_notification(
            new base_string("Parametrized type already defined"), this, secondary);
          return new error_signal(primary_notification, false);
        }
        master.bind_parametrized((parametrized_type) result_type, parameter_values);
      }

      maybe_add_default_supertype();
    }

    if (body != null) {
      for (int i = 0; i < body.size(); ++i) {
        analyze_and_ignore_errors(body.get(i), pass);
      }
    }

    if (pass == analysis_pass.SUPERTYPE_DECL) {
      flavor_profile the_profile = analyze_flavor_profile();
      if (master.has_flavor_profile()) {
        if (master.get_flavor_profile() != the_profile) {
          // TODO: format the notification better.
          new base_notification(new base_string("Wrong flavor profile for " + master + ": new " +
              the_profile + ", original " + master.get_flavor_profile()), this).report();
        }
      } else {
        master.set_flavor_profile(the_profile);
      }

      if (has_parameters() && !result_type.has_flavor_profile()) {
        result_type.set_flavor_profile(the_profile);
      }
    }

    if (pass == analysis_pass.METHOD_AND_VARIABLE_DECL) {
      if (result_type.get_pass().is_before(declaration_pass.METHODS_AND_VARIABLES)) {
        assert !declaration_analysis_in_progress;
        declaration_analysis_in_progress = true;
        result_type.process_declaration(declaration_pass.METHODS_AND_VARIABLES);
        declaration_analysis_in_progress = false;
      }
    }

    return null;
  }

  @Override
  public void process_declaration(declaration_pass pass) {
    if (declaration_analysis_in_progress) {
      return;
    }

    if (pass == declaration_pass.TYPES_AND_PROMOTIONS) {
      multi_pass_analysis(analysis_pass.SUPERTYPE_DECL);
    } else if (pass == declaration_pass.METHODS_AND_VARIABLES) {
      //if (!has_processed(analysis_pass.METHOD_AND_VARIABLE_DECL)) {
      // TODO: this needs to be fixed.
      if (!in_progress)
      {
        multi_pass_analysis(analysis_pass.METHOD_AND_VARIABLE_DECL);
      } else {
        if (DEBUG.in_progress_declaration) {
          log.debug("In progress mv: " + this + " lp " + last_pass);
        }
        //utilities.panic("In progress mv: " + this);
      }
      //}
    } else {
      utilities.panic("Unknown pass: " + pass);
    }
  }

  private void maybe_add_default_supertype() {
    if (!has_supertype_declarations() &&
        !get_kind().is_namespace() &&
        result_type != library().entity_type() &&
        result_type != library().value_type()) {

      // TODO: move default supertype to semantics
      principal_type default_supertype = library().value_type();

      assert body != null;
      position pos = this;
      add_to_body(new supertype_analyzer(
          analyzable_action.from_value(default_supertype, pos), pos));
    }

    /*
    if (false && get_kind() == type_kinds.procedure_kind &&
        has_parameters() &&
        short_name() == common_library.function_name) {
      log.debug("FUN: " + result_type);
      type procedure_supertype = library().procedure_type().bind_parameters(parameter_values).
          get_flavored(flavors.immutable_flavor);
      assert body != null;
      position pos = this;
      add_to_body(new supertype_analyzer(procedure_supertype, pos));
    }
    */
  }

  // TODO: move this error-explaining component
  private error_signal primary_already_defined() {
    @Nullable declaration primary_declaration = master.get_primary().get_declaration();
    @Nullable notification primary_notification = primary_declaration != null ?
        new base_notification("This is the primary", primary_declaration) : null;
    notification already_notification = new base_notification(
        new base_string("Primary type already defined"), this,
        primary_notification != null ? new base_list<notification>(primary_notification) : null);
    return new error_signal(already_notification, false);
  }

  private void add_to_body(analyzable the_analyzable) {
    list<analyzable> new_body = new base_list<analyzable>(the_analyzable);
    new_body.append_all(body);
    body = new_body;
  }

  private flavor_profile analyze_flavor_profile() {
    flavor_profile result = get_kind().default_profile();

    for (int i = 0; i < body.size(); ++i) {
      analyzable a = body.get(i);
      if (a instanceof supertype_analyzer && !has_errors(a)) {
        flavor_profile super_profile = action_utilities.get_profile((supertype_analyzer) a);
        result = flavor_profiles.combine(result, super_profile);
      }
    }

    return result;
  }

  @Override
  protected action do_get_result() {
    list<action> subactions = new base_list<action>();
    // TODO: execute static
    for (int i = 0; i < body.size(); ++i) {
      analyzable the_analyzable = body.get(i);
      if (the_analyzable instanceof variable_analyzer &&
          ((variable_analyzer) the_analyzable).get_category() == variable_category.STATIC) {
        subactions.append(analyzer_utilities.to_action(the_analyzable));
      }
    }

    // TODO: optimize?...
    return new list_action(subactions, this);
  }

  @Override
  public string to_string() {
    return utilities.describe(this, short_name());
  }
}
