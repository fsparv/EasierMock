Easier Mock
===========

[![Flattr this git repo](http://api.flattr.com/button/flattr-badge-large.png)](https://flattr.com/submit/auto?user_id=fsparv&url=https://github.com/fsparv/EasierMock&title=EasierMock&language=&tags=github&category=software)

This is a small add on to the wonderful [EasyMock](http://www.easymock.org/) library. Although easy mock is relatively
slick and was a big improvement in terms of ease of setup for mock objects, I have chosen a particular usage pattern
and boiled away the remaining boiler plate code using annotations. You may use it according to the terms of the
Apache Software Licesnse version 2.0 (see LICENSE.txt for details). The examples below are written for Junit
but recent tests seem to indicate that everything except Beantester.java works on TestNG as well.

Installation
-----
Simply list us as a dependency in your gradle/ivy/maven build that points at mavenCentral()

    dependencies {
       testCompile 'com.needhamsoftware:easier-mock:2.0'
    }


Examples
--------
For example to test that addNode properly sets sets itself as the parent on the node passed to it you could do this:


    public class TestNode {
      private Node node = new Node();
      @Mock private Node child;

      public TestNode() {
        prepareMocks(this);
      }

      @Test public void testAddNode() {
        reset();
        child.setParent(node);
        replay();
        node.addNode(child);
        verify();
      }
    }

Hard to get much simpler than that. I often put `reset()` in `@Before` and `verify()` in `@After` as well.

Another common problem is that the object under test is making calls to sub-methods on itself and you want
the unit tests for those to handle the complexity there and insulate the present test from failures in
that other method. The traditional way of handling that is to make a subclass of the class you want to
test and overide the method that you don't want to call. And if you need to do this for different methods for different
tests in the same test class, you get into custom setup code for separate tests and the only place to do that is
in the test method itself which in turn bloats the test method and makes it hard to read and maintain. I've had cases
my test method had 100 lines of anonymous sub-class and 10 lines of actual test code.

Untangling that knot and doing away with the whole subclassing issue is the purpose of the `@ObjectUnderTest`
annotation. A field that annotation will be populated with a special mock. This special type of mock allows
exactly one method call after replay() to pass through unintercepted. Any further method calls (including recursions!)
will again go to the mock. This looks something like this:

    public class TestNode {
      @ObjectUnderTest private Node node;
      @Mock private Node child;

      public TestNode() {
        prepareMocks(this);
      }

      @Before public void setUp() { reset(); }
      @After public void tearDown() { verify(); }

      @Test (expected = IOException.class) public void testAddNode() {
        expect(child.getId()).andReturn(42);
        expect(node.invokeMessaging(NODE_ADDED, 42)).andThrow(new IOException());
        replay();
        node.addNode(child);
      }
    }

Credits
-------

This code was originally developed by Patrick Heck at [Copyright Clearance Center](http://www.copyright.com) in Danvers MA, USA in 2011. That code was
[released on google code](https://code.google.com/p/easier-test/) under an Apache 2.0 license. After Patrick Heck left
Copyright Clearance Center he started [Needham Software LLC](http://www.needhamsoftware.com/) and in order to
use this code writing tests for his clients, he forked the unmaintained version on google code and added
Java 6 compatability. In June 2013 after using it yet again on another project he released it on Maven Central to make
it easy to use in build files for himself and the rest of the world.
