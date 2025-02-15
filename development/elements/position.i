-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Position is a way to reference almost any piece of data in the ideal system.
--- At the lowest level, |position| is an instance of
--- |ideal.development.scanners.text_position|; one level up, it's a |token|;
--- the next level up, it's a |construct|; at the top level, it's an |analyzable|
--- instance.  This heirarchy can be traversed by accessing |source_position| until
--- |null| is encountered.

interface position {
  extends data, reference_equality, stringable;

  position or null source_position;
}
