package wandledi.scala.test

import java.io.File
import java.io.FileReader
import java.io.IOException
import java.io.Reader
import java.io.StringReader
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import org.scalatest.Spec
import org.scalatest.matchers.ShouldMatchers
import org.w3c.dom.Document
import org.wandledi.Resources
import org.wandledi.Scroll
import org.wandledi.Wandler
import org.wandledi.scala.Selectable
import org.wandledi.spells.SpotMapping
import org.wandledi.spells.TextTransformation
import org.xml.sax.InputSource
import scala.xml.NodeSeq
import scala.xml.XML

class Semantics extends Spec with ShouldMatchers {
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

  describe("scala.Selectable") {
    it("should provide a functioning Element per #get") {
      val doc = transform("test.xhtml") { page => import page._
        val e = get("div")
        val offset = 1
        e.at(offset).setAttribute("foo", "bar")
      }
      val divs = doc \\ "div"
      divs should have size (3)
      divs(0).attribute("foo") should not be ('defined)
      divs(2).attribute("foo") should not be ('defined)
      divs(1).attribute("foo") should be ('defined)
      divs(1).attribute("foo").get(0).text should equal ("bar")
    }

    it("should provide a functioning SelectableElement per #at") {
      val doc = transform("test.xhtml") { page => import page._
        val e = at("div")
        val offset = 2
        e.at(offset).setAttribute("foo", "bar")
      }
      val divs = doc \\ "div"
      divs should have size (3)
      divs(0).attribute("foo") should not be ('defined)
      divs(1).attribute("foo") should not be ('defined)
      divs(2).attribute("foo") should be ('defined)
      divs(2).attribute("foo").get(0).text should equal ("bar")
    }

    it("should support context sensitive selection ($)") {
      val doc = transform("test.xhtml") { page => import page._
        $("div").setAttribute("foo", "bar")
        $$(".info") {
          $("div").setAttribute("color", "red") // should only apply to the nested div
        }
        // the previous statement should have the same effect as the following:
        // at(".info").get("div").setAttribute("color", "red")
      }
      val divs = doc \\ "div"
      divs.foreach(_.attribute("foo").get.head.text should equal ("bar"))
      val redDivs = divs.filter(div => div.attribute("color").isDefined)
      redDivs should have size (1)
      redDivs.head.text should equal ("Repeat: ")
    }

    it("should make it possible to switch context via #using") {
      val doc = transform("entities.xhtml") { page => import page._
        $("p").includeFile("inclusion.xhtml") { page =>
          using(page) {
            $("p").setAttribute("color", "red")
          }
        }
      }
      val p = (doc \\ "p").headOption
      p should be ('defined)
      p.get.text should include ("inclusion")
      p.get.attribute("color") should be ('defined)
    }
  }

  describe("scala.Element") {
    it("should support the foreach transformation") {
      val titles = List("It's", "something", "only", "you", "can", "take.")
      val doc = transform("test.xhtml") { page => import page._
        $("h1").foreachWithIndexIn(titles) { (e, item, index) =>
          e.replace(true, item)
          if (index % 2 == 1) {
            e.setAttribute("style", "background-color: red;")
          } else {
            e.setAttribute("style", "background-color: blue;")
          }
        }
      }
      val headings = doc \\ "h1"
      headings.size should be (titles.size)
      headings.zipWithIndex.foreach { tuple =>
        val (h1, index) = tuple
        val expected = if (index % 2 == 1) "red" else "blue"
        h1.attribute("style").get(0).text should include (expected)
      }
    }
  }

  describe("org.wandledi.spells.TextTransformation") {
    it("should support index-based 'spot' insertion") {
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
    it("should support regex-based 'spot' insertion") {
      val insertions = Map("Mar.+" -> "Heinz", "(T|t)he [a-zA-z]+.*" -> "Vertigo", "Spam.Free" -> "SpareTV")
      val doc = transform("strings.xhtml") { page => import page._
        val ttf = new TextTransformation(insertions.map(m =>
            new SpotMapping(m._1, true, Array(m._2): _*)).toArray: _*)
        page.at("#parenthesis").get(".text").cast(ttf)
      }
      val div = (doc \\ "div").filter(_.check("id" -> "parenthesis"))
      div should have size (1)
      val text = (div \ "p")(0).text
      text should not include ("(Markus)")
      text should include ("Heinz")
      text should include ("SpareTV")
      text should include ("Vertigo")
    }
    it("should support name-based 'spot' insertion") {
      val insertions = Map("Mar*" -> "Heinz", "*Ugly" -> "Vertigo", "Spam4Free" -> "SpareTV")
      val doc = transform("strings.xhtml") { page => import page._
        val ttf = new TextTransformation(insertions.map(m =>
            new SpotMapping(m._1, Array(m._2): _*)).toArray: _*)
        page.at("#parenthesis").get(".text").cast(ttf)
      }
      val div = (doc \\ "div").filter(_.check("id" -> "parenthesis"))
      div should have size (1)
      val text = (div \ "p")(0).text
      text should not include ("(Markus)")
      text should include ("Heinz")
      text should include ("SpareTV")
      text should include ("Vertigo")
    }
    it("should support regex-based insertion") {
      val doc = transform("strings.xhtml") { page => import page._
        val ttf = new TextTransformation("(T|t)he [a-zA-z]+", "Gals")
        page.at("#parenthesis").get(".text").cast(ttf)
      }
      val div = (doc \\ "div").filter(_.check("id" -> "parenthesis"))
      div should have size (1)
      val text = (div \ "p")(0).text
      text should not include ("the Ugly")
      text should include ("(Gals, Gals and Gals)")
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
  
  def wandle(file: String, scroll: Scroll): Option[String] = {
    val wandler = new Wandler
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
