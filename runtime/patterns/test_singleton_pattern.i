-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class test_singleton_pattern {

  testcase test_match() {
    the_pattern : singleton_pattern[character].new('x');

    assert the_pattern("x");
    assert !the_pattern("y");
    assert !the_pattern("xx");
  }

  testcase test_viable_prefix() {
    the_pattern : singleton_pattern[character].new('x');

    assert the_pattern.is_viable_prefix("");
    assert the_pattern.is_viable_prefix("x");
    assert !the_pattern.is_viable_prefix("y");
    assert !the_pattern.is_viable_prefix("xx");
  }

  testcase test_find_in() {
    the_pattern : singleton_pattern[character].new('x');

    assert the_pattern.find_in("", 0) is null;
    assert the_pattern.find_in("foo", 0) is null;
    assert the_pattern.find_in("xfoo", 1) is null;

    match : the_pattern.find_in("x", 0);
    assert match is_not null;
    assert match.begin == 0;
    assert match.end == 1;

    match2 : the_pattern.find_in("xyzzyxy", 2);
    assert match2 is_not null;
    assert match2.begin == 5;
    assert match2.end == 6;
  }

  testcase test_split() {
    the_pattern : singleton_pattern[character].new('x');

    split0: the_pattern.split("foo");
    assert split0.size == 1;
    assert equals(split0[0], "foo");

    split1: the_pattern.split("fooxbarx");
    assert split1.size == 3;
    assert equals(split1[0], "foo");
    assert equals(split1[1], "bar");
    assert equals(split1[2], "");

    split2: the_pattern.split("x1x2x3");
    assert split2.size == 4;
    assert equals(split2[0], "");
    assert equals(split2[1], "1");
    assert equals(split2[2], "2");
    assert equals(split2[3], "3");
  }

  -- TODO: This hack shouldn't be needed.
  boolean equals(immutable list[character] s0, string s1) {
    -- deeply_immutable list[character] dil : s0;
    return (s0 as string) == s1;
  }
}
