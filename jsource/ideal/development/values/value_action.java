/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.values;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.types.*;
import ideal.development.actions.*;
import ideal.development.declarations.*;

import javax.annotation.Nullable;

public class value_action<T extends base_data_value> extends base_value_action<T> {

  public value_action(T the_value, position source) {
    super(the_value, source);
  }

  @Override
  public abstract_value result() {
    return the_value;
  }

  @Override
  public action bind_from(action from, position pos) {
    return ((abstract_value) the_value.bind_from(from, pos)).to_action(pos);
  }

  @Override
  public @Nullable declaration get_declaration() {
    return the_value.get_declaration();
  }
}
