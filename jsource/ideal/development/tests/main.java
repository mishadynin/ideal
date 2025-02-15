/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.tests;

import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
import ideal.runtime.patterns.*;
import ideal.runtime.resources.*;
import ideal.runtime.channels.*;
import ideal.runtime.graphs.*;

import junit.framework.Test;
import junit.framework.TestSuite;

public class main {

  public static void main(String[] args) {
    run_all_runtime_tests();

    TestSuite suite = new TestSuite();

    // developer.notifications
    suite.addTestSuite(position_printer_t.class);

    // developer.names
    suite.addTestSuite(names_t.class);

    suite.addTestSuite(flag_util_t.class);

    junit.textui.TestRunner.run(suite);
  }

  public static void run_all_runtime_tests() {
    new test_array().run_all_tests();
    new test_runtime_util().run_all_tests();
    new test_string_writer().run_all_tests();

    new test_list().run_all_tests();
    new test_range().run_all_tests();
    new test_dictionary().run_all_tests();
    new test_hash_dictionary().run_all_tests();
    new test_hash_set().run_all_tests();

    new test_elements().run_all_tests();
    new test_plain_text().run_all_tests();
    new test_markup_text().run_all_tests();

    new test_singleton_pattern().run_all_tests();

    new test_resolver().run_all_tests();

    new test_output_transformer().run_all_tests();

    new test_graph().run_all_tests();
  }
}
