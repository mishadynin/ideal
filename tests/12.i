one : 1;
two : 2;
class cool {
  static var integer sfield;
  static var integer sfield2;
  var integer field;
  cool() {
    field = 68;
    sfield = 680;
  }
}
cool.sfield2 = 4242;
cool x : cool.new();
println(cool.sfield);
println(x.field);
println(cool.sfield2);

integer three : plus(one, two);
--int four() { return plus(two, two); }
integer double(integer x) => plus(x, x);
integer add5(integer y) => plus(y, 5);
integer constant() => 34;
--println(plus(double(two), four()));
--four();
constant();
println(((6868)));
--json: [a: 'foo', b: 5, c: [1, 2, 3]];
--Checking paramteres with initializers.
println('foo' : 5 * 5);
println(constant());
println(double(double(three)));
println(plus(add5(three), two));
println(7, " ", add5(63));
{ println(one, " ", plus(one, one), " ", one); }
please println("hello " ++ "world");
string triple(string s) { return s ++ s ++ s; }
println(triple("Hurrah! "));
string tt()
  #(h1 "Hello " (a (name "http://google.com") "world!<>&"))
string tt2()
  #(h1 "Hello " (h2 "world!"))
println(tt());
class typefoo {
  static string baz : "Hey baz.";
  static string method() {
    return "xxx" ++ baz;
  }
}
println(typefoo.method());

class new_type {
  implements value;

  string field;
  overload new_type() { }
  overload new_type(string val) => field = val;
  string get_state() => "state: " ++ field;
}
println(new_type.new("hey!").get_state());

class sub_type {
  extends new_type;
  sub_type(string v2) {
    --super();
  }
  void some_method(value arg) {
    (arg as new_type).field;
  }
}
new_type val : sub_type.new("hahaha");
println((val is sub_type) ? "yeah" : "nay");
void test_loop() {
  loop {
    println("Hey.");
  }
}
-- test_loop();
void test_assignment() {
  var nonnegative non : 1;
  non += 2;
  var integer may : 3;
  may += 2;
  integer test : 4;
  assert test is nonnegative;
  nonnegative narrowed : test;

  string or null test_string : "123";

  if (test_string is string) {
    println(test_string.size);
  }

  if (test_string is null) {
    for (; false; 3) { }

    println("null...");
  } else {
    println(test_string.size);
  }
}
