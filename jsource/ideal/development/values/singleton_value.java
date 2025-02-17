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
import ideal.development.kinds.*;
import ideal.development.flavors.*;

public class singleton_value extends base_data_value {

  public singleton_value(principal_type singleton_type) {
    super(singleton_type.get_flavored(flavors.deeply_immutable_flavor));
    if (singleton_type.get_kind() != type_kinds.singleton_kind) {
      utilities.panic("Not a singleton type: " + singleton_type);
    }
  }

  public string to_string() {
    return new base_string(type_bound().to_string(), ".",
        type_kinds.INSTANCE_NAME.to_string());
  }
}
