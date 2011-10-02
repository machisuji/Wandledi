package wandledi.scala.test

import java.io.File
import java.io.FileReader
import java.io.IOException
import java.io.Reader
import java.io.StringReader
import java.io.StringWriter

import org.testng.annotations._
import org.testng.Assert._

import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Document
import org.xml.sax.InputSource
import scala.xml.NodeSeq
import scala.xml.XML

import org.wandledi.{Selectable => _, _}
import org.wandledi.spells.SpotMapping
import org.wandledi.spells.TextTransformation
import org.wandledi.scala._

/**
 * This suite also tests the Java API, so the Java build is only ok if these tests pass, too!
 * I've put them here, because it's simply more convenient to write tests in Scala than in Java.
 * Even when not using ScalaTest.
 */
class Semantics {

  val testFileDirectory = "core/src/test/java/wandledi/test/"

  def transform(file: String, debug: Boolean = false)(magic: (Selectable) => Unit): NodeSeq = {
    val page = Selectable(new Scroll)
    magic(page)
    val result = wandle(file, page.getScroll)
    result should be ('defined)
    if (debug) println(result.get)
    XML.loadString(result.get)
  }

  implicit def pimped(node: xml.Node) = new {
    def check(attr: (String, String)) = node.attribute(attr._1).exists(_.exists(_.text == attr._2))
  }

  implicit def pimped(nodeSeq: xml.NodeSeq) = new {
    def \(attr: (String, String)) =
      nodeSeq.filter(_.attribute(attr._1).exists(_.exists(_.text == attr._2)))
  }

  /**
   * scala.Selectable should provide a functioning Element per #get
   */
  @Test
  def selectableGet {
    val doc = transform("test.xhtml") { page => import page._
      val e = get("div")
      val offset = 1
      e.at(offset).setAttribute("foo", "bar")
    }
    val divs = doc \\ "div"
    assertEquals(3, divs.size, "number of divs")

    assertFalse(divs(0).attribute("foo").isDefined, "foo is defined")
    assertFalse(divs(2).attribute("foo").isDefined, "foo is defined")
    assertTrue(divs(1).attribute("foo").isDefined, "foo should be defined")
    assertEquals("bar", divs(1).attribute("foo").get(0).text, "attribute foo")
  }

  /**
   * scala.Selectable should provide a functioning SelectableElement per #at
   */
  @Test
  def selectableAt {
    val doc = transform("test.xhtml") { page => import page._
      val e = at("div")
      val offset = 2
      e.at(offset).setAttribute("foo", "bar")
    }
    val divs = doc \\ "div"
    assertEquals(3, divs.size, "number of divs")

    assertFalse(divs(0).attribute("foo").isDefined, "foo is defined")
    assertFalse(divs(1).attribute("foo").isDefined, "foo is defined")
    assertTrue(divs(2).attribute("foo").isDefined, "foo is defined")
    assertEquals("bar", divs(2).attribute("foo").get(0).text, "attribute foo")
  }

  /**
   * scala.Selectable should support context sensitive selection ($)
   */
  @Test
  def contextSensitiveSelection {
    val doc = transform("test.xhtml") { page => import page._
      $("div").setAttribute("foo", "bar")
      $$(".info") {
        $("div").setAttribute("color", "red") // should only apply to the nested div
      }
      // the previous statement should have the same effect as the following:
      // at(".info").get("div").setAttribute("color", "red")
    }
    val divs = doc \\ "div"
    val redDivs = divs.filter(div => div.attribute("color").isDefined)

    divs.foreach(div =>
      assertEquals("bar", div.attribute("foo").get.head.text), "attribute foo")

    assertEquals(1, redDivs.size, "number of red divs")
    assertEquals("Repeat: ", redDivs.head.text, "red divs' text")
  }

  /**
   * scala.Selectable should make it possible to switch context via #using
   */
  @Test
  def contextSensitiveSelection2 {
    val doc = transform("selectors.xhtml") { page => import page._
      $("p").includeFile("inclusion.xhtml") { page =>
        using(page) {
          $("p").setAttribute("color", "red")
        }
      }
    }
    val p = (doc \\ "p").headOption
    assertTrue(p.isDefined, "p is defined")
    p.foreach { p =>
      assertTrue(p.text contains "inclusion", "p contains 'inclusion'")
      assertTrue(p.attribute("color").isDefined, "attribute color is defined")
    }
  }

  /**
   * scala.Element should support the foreach transformation
   */
  @Test
  def elementForeach {
    val titles = List("It's", "something", "only", "you", "can", "take.")
    val doc = transform("test.xhtml") { page => import page._
      $("h1").foreachWithIndexIn(titles) { (e, item, index) =>
        e.replace(true, item)
        if (index % 2 == 1) e.setAttribute("style", "background-color: red;")
        else e.setAttribute("style", "background-color: blue;")
      }
    }
    val headings = doc \\ "h1"
    assertEquals(titles.size, headings.size, "number of headings")
    headings.zipWithIndex.foreach { case (h1, index) =>
      val expected = if (index % 2 == 1) "red" else "blue"
      assertTrue(h1.attribute("style").head.text contains expected, "color is "+expected)
    }
  }

  /**
   * scala.Element should accept xml.NodeSeqs as insertions and as replacements
   */
  @Test
  def elementInsertReplaceXml {
    val doc = transform("test.xhtml") { page => import page._
      $("body").insert(false, <pre id="insertion">Quid Quo Pro</pre>)
      $(".info").replace(true, <p>Quid Quo Pro</p>)
    }
    val pre = doc \\ "pre"
    assertEquals(1, pre.size, "numbers of pre's")
    assertEquals("Quid Quo Pro", pre.head.text, "pre's text")
    val p = doc \\ "p"
    assertEquals(1, p.size, "numbers of p's")
    assertEquals("Quid Quo Pro", p.head.text, "p's text")
  }

  /**
   * TextTransformation should support index-based 'spot' insertion
   */
  @Test
  def ttIndexBasedSpots {
    val insertions = Array("Hans", "FunFilms", "Airplane!")
    val doc = transform("strings.xhtml") { page => import page._
      val ttf = new TextTransformation(insertions)
      page.at("#parenthesis").get(".text").cast(ttf)
    }
    val div = (doc \\ "div").filter(_.check("id" -> "parenthesis"))
    div should have size (1)
    val text = (div \ "p")(0).text
    text should not include ("(")
    text should not include (")")
    insertions.foreach(text should include (_))
  }

  /**
   * TextTransformation should support regex-based 'spot' insertion
   */
  @Test
  def ttRegexBasedSpots {
    val insertions = Map("Mar.+" -> "Heinz", "(T|t)he [a-zA-z]+.*" -> "Vertigo", "Spam.Free" -> "SpareTV")
    val doc = transform("strings.xhtml") { page =>
      val ttf = new TextTransformation(insertions.map(m =>
        new SpotMapping(m._1, true, Array(m._2): _*)).toArray: _*)
      page.at("#parenthesis").get(".text").cast(ttf)
    }
    val div = (doc \\ "div").filter(_.check("id" -> "parenthesis"))
    assertEquals(1, div.size, "number of divs")
    val text = (div \ "p").head.text
    assertFalse(text contains "(Markus)", "text contains '(Markus)'")
    assertTrue(text contains "Heinz", "text contains 'Heinz'")
    assertTrue(text contains "SpareTV", "text contains 'SpareTV'")
    assertTrue(text contains "Vertigo", "text contains 'Vertigo'")
  }

  /**
   * TextTransformation should support name-based 'spot' insertion
   */
  @Test
  def ttNameBasedSpots {
    val insertions = Map("Mar*" -> "Heinz", "*Ugly" -> "Vertigo", "Spam4Free" -> "SpareTV")
    val doc = transform("strings.xhtml") { page =>
      val ttf = new TextTransformation(insertions.map(m =>
        new SpotMapping(m._1, Array(m._2): _*)).toArray: _*)
      page.at("#parenthesis").get(".text").cast(ttf)
    }
    val div = (doc \\ "div").filter(_.check("id" -> "parenthesis"))
    assertEquals(1, div.size, "number of divs")
    val text = (div \ "p")(0).text
    assertFalse(text contains "(Markus)", "text contains '(Markus)'")
    assertTrue(text contains "Heinz", "text contains 'Heinz'")
    assertTrue(text contains "SpareTV", "text contains 'SpareTV'")
    assertTrue(text contains "Vertigo", "text contains 'Vertigo'")
  }

  /**
   * TextTransformation should support regex-based insertion
   */
  @Test
  def ttRegexBasedInsertion {
    val doc = transform("strings.xhtml") { page =>
      val ttf = new TextTransformation("(T|t)he [a-zA-z]+", "Gals")
      page.at("#parenthesis").get(".text").cast(ttf)
    }
    val div = (doc \\ "div").filter(_.check("id" -> "parenthesis"))
    assertEquals(1, div.size, "number of divs")
    val text = (div \ "p")(0).text
    assertFalse(text contains "the Ugly", "text contains 'the Ugly'")
    assertTrue(text contains "(Gals, Gals and Gals)", "text contains '(Gals, Gals and Gals)'")
  }
    it("should support regex-based insertion where the regex contains a single capturing group") {
      val doc = transform("strings.xhtml") { page => import page._
        val ttf = new TextTransformation("The Good, the (bad) and the Ugly", "Bad")
        page.at("#parenthesis").get(".text").cast(ttf)
      }
      val div = (doc \\ "div").filter(_.check("id" -> "parenthesis"))
      div should have size (1)
      val text = (div \ "p")(0).text
      text should not include ("the bad")
      text should include ("(Bad)")
    }
    it("should only affect the target element's text and not those of nested elements.") {
      val doc = transform("strings.xhtml") { page => import page._
        val ttf = new TextTransformation("[a-zA-Z0-9!_,\\.]", "_")
        page.at("#upuntilnow").get(".text").cast(ttf)
      }
      val div = doc \\ "div" \ ("id" -> "upuntilnow")
      div should have size (1)
      val text = (div \ "p")(0).text
      text.replace("_", "").trim should startWith ("Markus")
      text.replace("_", "").trim should endWith("Spam4Free")
      val spans = div \ "p" \ "span"
      val user = spans.find(_.check("id" -> "user")).getOrElse(fail())
      val flavour = spans.find(_.check("id" -> "flavour")).getOrElse(fail())
      user.text should equal ("Markus")
      flavour.text should equal ("Spam4Free")
    }
    it("should consider empty text if configured to do so") {
      val doc = transform("noText.xhtml") { page => import page._
        val tt = new TextTransformation(null, "foobar")
        tt.setConsiderEmptyText(true)
        page.get("#noText").cast(tt)
      }
      val p = doc \\ "p" \ ("id" -> "noText")
      p should have size (1)
      p(0).text should equal ("foobar")
    }
    it("and should ignore empty text per default") {
      val doc = transform("noText.xhtml") { page => import page._
        val tt = new TextTransformation(null, "foobar")
        page.get("#noText").cast(tt)
      }
      val p = doc \\ "p" \ ("id" -> "noText")
      p should have size (1)
      p(0).text should be ('empty)
    }


  describe("scala.TextContent") {
    val values = List("Hans", "FunFilms", "\"Airplane!\"")
    it("should correctly wrap all available TextTransformations") {
      val doc = transform("strings.xhtml") { page => import page._
        val runs = List('indexSpot, 'nameSpot, 'regexSpot, 'transformWholly,
            'transformRegex, 'replaceWholly)
        at("#parenthesis").get(".text").foreachIn(runs) { (p, run) =>
          run match {
            case 'indexSpot => p.text.insert(values: _*)
            case 'nameSpot =>
              p.text.insert("Markus" -> values(0), "*Free" -> values(1), "The*" -> values(2))
            case 'regexSpot =>
              p.text.insertR("Mar.u." -> values(0), ".+4.+" -> values(1), ".*" -> values(2))
            case 'transformWholly => p.text.transform(_.toUpperCase)
            case 'transformRegex => p.text.transform("We.+me")("Un" + _.toLowerCase)
            case 'replaceWholly => p.text = "Foobar"
            case _ =>
          }
        }
      }
      val ps = doc \\ "div" \ ("id" -> "parenthesis") \ "p" \ ("class" -> "text")
      ps should have size (6)

      for (i <- 0 to 2) {
        ps(i).text should not include ("(Markus)")
        values.foreach(ps(i).text should include (_))
      }
      ps(0) should equal (ps(1))
      ps(1) should equal (ps(2))

      ps(3).text.trim should startWith ("WELCOME")
      ps(4).text.trim should startWith ("Unwelcome")
      ps(5).text should equal ("Foobar")
    }
  }

  describe("Truncate") {
    it("should work within Extractions too") {
      val doc = transform("test.xhtml") { page => import page._
        get("title").includeFile("strings.xhtml")(using(_) {
          $(new PathSelector).extract("head")
          $("head").truncate(1)
          $("meta").hide()
        })
      }
      val title = doc \\ "title"
      val head = doc \\ "head"

      head should have size (1)
      title should have size (1)
      title(0).text should include ("String Insertion Test")
    }
  }

  describe("Selection") {
    val selectable = Selectable()
    import selectable.$
    it("should work with strings as CSS selectors") {
      val isCss = $("#content").getSelector.isInstanceOf[CssSelector]
      isCss should be (true)
    }
    it("should take Sequences for PathSelectors") {
      $(Seq[String]()).getSelector.getClass should equal ($(Seq()).getSelector.getClass)
      $(Seq()).getSelector.getClass should equal ($(Nil).getSelector.getClass)
      $(Nil).getSelector.getClass should equal (classOf[PathSelector])
    }
  }
  
  def wandle(file: String, scroll: Scroll): Option[String] = {
    val wandler = Wandler.forXHTML
    val output = new StringWriter
    var input: FileReader = null
    wandler.setResources(new Resources {
        def open(file: String): Reader = {
          new FileReader(new File(testFileDirectory, file))
        }
      })
    try {
      input = new FileReader(new File(testFileDirectory, file))
      wandler.useScroll(scroll)
      wandler.wandle(input, output)
      Some(output.toString)
    } catch {
      case e: IOException => e.printStackTrace; None
    } finally {
      if (input != null) {
        try {
          input.close
        } catch {
          case _ =>
        }
      }
    }
  }

  def parseXML(xml: String): Option[Document] = {
    try {
      val builder = DocumentBuilderFactory.newInstance.newDocumentBuilder
      val result = builder.parse(new InputSource(new StringReader(xml)))
      Some(result)
    } catch {
      case e: Exception => e.printStackTrace; None
    }
  }
}
