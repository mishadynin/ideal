/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.templates;

import ideal.library.elements.*;
import ideal.library.texts.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.runtime.texts.*;
import static ideal.runtime.texts.markup_formatter.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.types.*;
import ideal.development.constructs.*;
import ideal.development.extensions.*;
import ideal.development.flavors.*;
import ideal.development.modifiers.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.values.*;
import ideal.development.analyzers.*;

/**
 * The interface that hides the differences between element and attribute ids was
 * proposed by Erik Naggum.
 *
 * @see http://genius.cat-v.org/erik-naggum/lisp-markup
 */
public class attribute_handler implements sexpression_handler {
  private final attribute_id attribute;

  public attribute_handler(attribute_id attribute) {
    this.attribute = attribute;
  }

  public action_name name() {
    return simple_name.make(attribute.short_name());
  }

  public analyzable to_analyzable(readonly_list<construct> arguments, template_analyzer template,
      position source) {
    statement_list_analyzer result = new statement_list_analyzer(source);

    list<analyzable> subactions = new base_list<analyzable>();
    string attr_start = new base_string(ATTRIBUTE_SEPARATOR, attribute.short_name(),
        ATTRIBUTE_START);
    subactions.append(template.make_appender(attr_start, result));
    subactions.append_all(template.process_list(arguments));
    subactions.append(template.make_appender(ATTRIBUTE_END, result));

    result.populate(subactions);
    return result;
  }
}
