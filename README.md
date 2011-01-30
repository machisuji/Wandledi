Wandledi
========

Wandledi is an HTML transformation library which is supposed to provide
an alternative to all that template business.
See below for more details.

Wandledi used to be a web framework too.
But I have scrapped that.
Now Wandledi is only the library responsible for transforming HTML.
Instead of a whole web framework I created a tiny lib which integrates
Wandledi with arbitrary applications that base upon the Java Servlet API: Wandlet.


Repository structure
--------------------

* **core** - The actual Wandledi project
* **scala-lib** - Wandledi Wrapper for Scala
* **wandlet** - Wandledi for the Servlet API
* **wandlet-scala** - Wandlet for Scala

How it used to be
-----------------

There is this widely used approach where you have a controller,
which does the logic and fetches necessary data to hand it
over to a template file (jsp, erb, you name it).
So you always have two parts. The controller and the template.

In WandlediWeb for Scala this looked as follows.

**Controller**:

    import org.wandlediweb.scala.Controller

    class HomeController extends Controller {
      def index() {
        model += "wb" -> session.get("user").isDefined)
        model += "msg" -> "Paragraph No."
        model += "items" -> List("One", "Two", "Three")
      }
    }

**Template** (index.jsp):

    <%@page contentType="text/html" pageEncoding="UTF-8"%>
    <%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
    <html>
      <body>
        <c:if test="${wb}">
          Welcome back!
        </c:if>
        <c:foreach items="${items}" var="no">
          <p>${msg} ${no}</p>
        </c:foreach>        
      </body>
    </html>

We all know this. But personally I don't like it.
I don't like "programming" with a cumbersome replacement (read EL and/or JSTL)
for the actual programming language I'm working with.
These ugly if-tags and other directives not only clutter what's really important
(the HTML), but also they mess it up with "programmer's stuff" which not only
confuses HTML editing software like Dreamweaver but also your web designer.

The Wandledi approach
---------------------

So I'd rather like to keep the HTML clean and express stuff like if and loops
in a real programming language. Now let's see how this is done using Wandledi.
First Wandledi introduces a third member to the party after throwing out
the template and replacing it with plain HTML*.

Magic. That is Spells. Spell is just a funny word for transformation
I came up with because transformation had to many syllables for my taste
and overall I have the unhealthy habit of using overly figurative names.

At the moment the class that uses these spells is called Page.
Not very enchanting, but the namespace is pure chaos right now anyway.
The Page class provides methods to the actual controller to transform
every HTML page.

So this leaves us with the three parts: Controller, Page, HTML file.

\**XHTML to be more precise since currently I'm simply using SAX to parse
the pages.*

**Controller**:

    import org.wandlediweb.scala.PageController

    class HomeController extends PageController {
      val page = new HomePage
      override def getPage = page
        
      def index() {
        val wb = session.get("user").isDefined
        val msg = "Paragraph No."
        val items = List("One", "Two", "Three")
            
        page.index(wb, msg, items)
      }
    }
    
**Page**:

    import org.wandlediweb.scala.Page
    import org.wandlediweb.scala.Implicits.trailingConditionals
    
    class HomePage extends Page {
      def index(wb: Boolean, msg: String, items: Iterable[String]) {
        $("body").insert("Welcome back!") provided wb
        $("p").foreachIn(items) { e, item =>
          e.insertFirst(msg)
          e.insertLast(item)
        }
      }
    }
    
**XHTML file**:

    <html>
      <body>
        <p>Message!</p>
      </body>
    </html>
    
Now this is a little more code. After all you have a whole class more.
But hey, at least the HTML file got a little shorter.
Moreover it now contains only plain HTML, which can actually be displayed by a browser.

The result in both cases (with a logged in user) will be the following HTML file.

**Resulting HTML**:

    <html>
      <body>
        Welcome back!
        <p>Paragraph No. One</p>
        <p>Paragraph No. Two</p>
        <p>Paragraph No. Three</p>
      </body>
    </html>

Great, isn't it?
----------------

Well, I haven't found out yet, if and how great this is.
But as I continue this project I will try to build a medium sized
application with this approach and eventually I will see how it works out.

I think that having another layer between view and controller that is responsible
for how exactly everything is going to be displayed may hold several advantages
since you are able to apply all patterns and OOP techniques to it since it is
normal code now and no auxiliary between HTML and a programming language.

How is it coming along?
-----------------------

At the moment the repository is quite a mess with scattered code and
randomly named classes as I still haven't made a final decision
as to how to name everything.
Transformation, Spell, Wossname.
PageController, Grimoire, Wizard. Everything's possible.

As for the functionality: Everything should work now so that one can
build anything with Wandledi. It merely has to be documented how at some point.

Building Wandledi
-----------------

Everything is done via sbt (Simple Build Tool).

Using Wandledi with Scalatra
----------------------------

So instead of wasting my time tinkering my own little web framework,
why not use a proper one that's already there?
Lift is a little too much for me at the moment (besides it's got a somewhat
similar approach to the view).

I wanted something more lightweight and I've found it: Scalatra.
Here is an example for using Wandledi inside your Scalatra application.
It's based on the Scalatra prototype app.

    package com.example

    import org.scalatra._
    import java.net.URL
    import scalate.ScalateSupport
    import org.wandledi.wandlet.scala.{Wandlet, Page}

    class MyScalatraFilter extends ScalatraFilter with Wandlet {
      def getHttpServletResponse = response // required by Wandlet
      
      get("/") {
        render(new Index) // render is a method provided by Wandlet
      }
    }
    
    class Index extends Page("index.xhtml") {
      // write all the Wandledi stuff right here
      // what follows is just nonsense actually
      $("#someId").foreachIn(List("Friede", "Freude", "Eierkuchen")) { (e, item) =>
        e.get(".someClass").replace(true, item)
      }
      $("someElement").insertLast(new java.util.Date())
    }

That's it. As said before you can of course use Wandlet everywhere where the
Servlet API is used. You just need to subclass Wandlet and provide an implementation
for Wandlet#getHttpServletResponse.
Wandlet#render() will write the given page using the HttpServletRequests' Writer.
In a Servlet this could look something like this (a Java example this time):

    import org.wandledi.wandlet.Wandlet;

    public class MyServlet extends HttpServlet {
        private Wandlet wandlet = new Wandlet();
        
        public void doGet(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {
            wandlet.render(new Page("index.xhtml"), response);
        }
    }
