// Autogenerated from runtime/resources/test_resolver.i

package ideal.runtime.resources;

import ideal.library.elements.*;
import ideal.library.resources.*;
import ideal.runtime.elements.*;

public class test_resolver {
  public void run_all_tests() {
    ideal.machine.elements.runtime_util.start_test("test_resolver.test_file_catalogs");
    test_file_catalogs();
    ideal.machine.elements.runtime_util.end_test();
    ideal.machine.elements.runtime_util.start_test("test_resolver.test_simple_resolve");
    test_simple_resolve();
    ideal.machine.elements.runtime_util.end_test();
    ideal.machine.elements.runtime_util.start_test("test_resolver.test_resolve_extension");
    test_resolve_extension();
    ideal.machine.elements.runtime_util.end_test();
    ideal.machine.elements.runtime_util.start_test("test_resolver.test_sub_catalogs");
    test_sub_catalogs();
    ideal.machine.elements.runtime_util.end_test();
    ideal.machine.elements.runtime_util.start_test("test_resolver.test_root_subdirs");
    test_root_subdirs();
    ideal.machine.elements.runtime_util.end_test();
    ideal.machine.elements.runtime_util.start_test("test_resolver.test_multiple_subdirs");
    test_multiple_subdirs();
    ideal.machine.elements.runtime_util.end_test();
    ideal.machine.elements.runtime_util.start_test("test_resolver.test_more_subdir_ops");
    test_more_subdir_ops();
    ideal.machine.elements.runtime_util.end_test();
    ideal.machine.elements.runtime_util.start_test("test_resolver.test_parent_catalog");
    test_parent_catalog();
    ideal.machine.elements.runtime_util.end_test();
  }
  public static class test_store extends base_resource_store {
    public test_store(final string path_prefix, final boolean is_current) {
      super(path_prefix, is_current, is_current);
    }
    public @Override boolean exists(final immutable_list<string> path) {
      return false;
    }
    public @Override string read_string(final immutable_list<string> path) {
      return new base_string("test");
    }
    public @Override void write_string(final immutable_list<string> path, final string new_value) { }
    public @Override void make_catalog(final immutable_list<string> path) { }
  }
  public static final resource_catalog CURRENT = new test_store(resource_util.CURRENT_CATALOG, true).top();
  public static final resource_catalog ROOT = new test_store(resource_util.ROOT_CATALOG, false).top();
  public void test_file_catalogs() {
    assert ideal.machine.elements.runtime_util.values_equal(CURRENT.get_id().to_string(), new base_string("."));
    assert ideal.machine.elements.runtime_util.values_equal(ROOT.get_id().to_string(), new base_string("/"));
  }
  public void test_simple_resolve() {
    final string foo = new base_string("foo");
    assert ideal.machine.elements.runtime_util.values_equal(CURRENT.resolve(foo).to_string(), new base_string("foo"));
    assert ideal.machine.elements.runtime_util.values_equal(ROOT.resolve(foo).to_string(), new base_string("/foo"));
  }
  public void test_resolve_extension() {
    final string bar = new base_string("bar");
    final base_extension html = base_extension.HTML;
    assert ideal.machine.elements.runtime_util.values_equal(CURRENT.resolve(bar, html).to_string(), new base_string("bar.html"));
    assert ideal.machine.elements.runtime_util.values_equal(ROOT.resolve(bar, html).to_string(), new base_string("/bar.html"));
  }
  public void test_sub_catalogs() {
    final resource_catalog cat = CURRENT;
    final resource_identifier dog = cat.resolve(new base_string("dog"));
    assert ideal.machine.elements.runtime_util.values_equal(dog.to_string(), new base_string("dog"));
    final resource_catalog cat2 = dog.access_catalog();
    assert ideal.machine.elements.runtime_util.values_equal(cat2.get_id().to_string(), new base_string("dog"));
    final resource_identifier dog2 = cat2.resolve(new base_string("Yoshka"));
    assert ideal.machine.elements.runtime_util.values_equal(dog2.to_string(), new base_string("dog/Yoshka"));
  }
  public void test_root_subdirs() {
    final resource_catalog cat = ROOT;
    final resource_identifier dog = cat.resolve(new base_string("dog"));
    assert ideal.machine.elements.runtime_util.values_equal(dog.to_string(), new base_string("/dog"));
    final resource_catalog cat2 = dog.access_catalog();
    assert ideal.machine.elements.runtime_util.values_equal(cat2.get_id().to_string(), new base_string("/dog"));
    final resource_identifier dog2 = cat2.resolve(new base_string("Yoshka"));
    assert ideal.machine.elements.runtime_util.values_equal(dog2.to_string(), new base_string("/dog/Yoshka"));
  }
  public void test_multiple_subdirs() {
    resource_catalog foo = ROOT;
    resource_identifier bar = foo.resolve(new base_string("bar"));
    assert ideal.machine.elements.runtime_util.values_equal(bar.to_string(), new base_string("/bar"));
    foo = bar.access_catalog();
    assert ideal.machine.elements.runtime_util.values_equal(foo.get_id().to_string(), new base_string("/bar"));
    bar = foo.resolve(new base_string("baz"));
    assert ideal.machine.elements.runtime_util.values_equal(bar.to_string(), new base_string("/bar/baz"));
    foo = bar.access_catalog();
    bar = foo.resolve(new base_string("quux"));
    assert ideal.machine.elements.runtime_util.values_equal(bar.to_string(), new base_string("/bar/baz/quux"));
    foo = bar.access_catalog();
    bar = foo.resolve(new base_string(""));
    assert ideal.machine.elements.runtime_util.values_equal(bar.to_string(), new base_string("/bar/baz/quux"));
    bar = foo.resolve(new base_string("./././."));
    assert ideal.machine.elements.runtime_util.values_equal(bar.to_string(), new base_string("/bar/baz/quux"));
  }
  public void test_more_subdir_ops() {
    resource_catalog foo = CURRENT;
    resource_identifier bar = foo.resolve(new base_string("foo/bar/././baz"));
    assert ideal.machine.elements.runtime_util.values_equal(new base_string("foo/bar/baz"), bar.to_string());
    foo = bar.access_catalog();
    bar = foo.resolve(new base_string(".."));
    assert ideal.machine.elements.runtime_util.values_equal(new base_string("foo/bar"), bar.to_string());
    foo = bar.access_catalog();
    bar = foo.resolve(new base_string("../.."));
    assert ideal.machine.elements.runtime_util.values_equal(new base_string("."), bar.to_string());
    foo = bar.access_catalog();
    bar = foo.resolve(new base_string(".."));
    assert ideal.machine.elements.runtime_util.values_equal(new base_string("."), bar.to_string());
  }
  public void test_parent_catalog() {
    final resource_catalog foo = CURRENT;
    resource_identifier bar = foo.resolve(new base_string("foo/bar/././baz"));
    assert ideal.machine.elements.runtime_util.values_equal(new base_string("foo/bar/baz"), bar.to_string());
    bar = bar.parent();
    assert ideal.machine.elements.runtime_util.values_equal(new base_string("foo/bar"), bar.to_string());
    bar = bar.parent();
    assert ideal.machine.elements.runtime_util.values_equal(new base_string("foo"), bar.to_string());
    bar = bar.parent();
    assert ideal.machine.elements.runtime_util.values_equal(new base_string("."), bar.to_string());
    bar = bar.parent();
    assert ideal.machine.elements.runtime_util.values_equal(new base_string("."), bar.to_string());
  }
}
