package wandledi.scala.test

import java.io.File
import java.io.FileReader
import java.io.IOException
import java.io.Reader
import java.io.StringReader
import java.io.StringWriter

import org.scalatest._

import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Document
import org.xml.sax.InputSource
import scala.xml.NodeSeq
import scala.xml.XML

import org.wandledi.{Selectable => _, _}
import org.wandledi.spells.SpotMapping
import org.wandledi.spells.TextTransformation
import org.wandledi.scala._
import wandlet.scala.Page

class Semantics extends FunSpec with Matchers {
  val testFileDirectory = "core/src/test/java/wandledi/test/"

  def transform(file: String, debug: Boolean = false)(magic: (Selectable) => Unit): NodeSeq = {
    val page = Selectable(new Scroll)
    magic(page)
    val result = wandle(file, page.getScroll)
    result should be ('defined)
    if (debug) println(result.get)
    XML.loadString(result.get)
  }

  def attribute(attr: (String, String))(node: xml.Node) = node.attribute(attr._1).exists(_.exists(_.text == attr._2))

  implicit def pimped(node: xml.Node) = new {
    def check(attr: (String, String)) = attribute(attr)(node)
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

    it("should not mix up local spell order") {
      val rows = List("Something", "about", "us")
      val page = new org.wandledi.scala.SelectableImpl(new Scroll) {
        $(Nil).extract("table.data")
        $$("table.data") {
          $("tr.transaction").foreachIn(rows, reduceBefore = true) { (tr, row) =>
            $("tr").replace(true, row)
          } // adds two local spells: reduction, then duplication - not the other way around
        }
      }
      val doc = XML.loadString(wandle("wgadmin.html", page.getScroll).get)
      val trs = (doc \\ "tr").filter(attribute("class" -> "transaction"))

      trs should have size (rows.size)
      trs.zip(rows).foreach { case (tr, row) =>
        tr.text.trim should equal (row)
      }
    }

    it("should make it possible to switch context via #using") {
      val doc = transform("selectors.xhtml") { implicit page => import page._
        $("p").includeFile("inclusion.xhtml") {
          $("p").setAttribute("color", "red")
        }
      }
      val p = (doc \\ "p").headOption
      p should be ('defined)
      p.get.text should include ("inclusion")
      p.get.attribute("color") should be ('defined)
    }

    it("should be used (and usable) for modules") {
      trait Module extends Selectable {
        def clearBody() {
          $("body").replace(true, "")
        }
      }
      val page = new org.wandledi.scala.SelectableImpl(new Scroll) with Module {
        clearBody()
      }
      val doc = XML.loadString(wandle("test.xhtml", page.getScroll).get)
      val body = doc \\ "body" head

      body.text.trim should equal ("")
      body.child should have size (1)
      body.child.head.isInstanceOf[xml.Text] should be (true)
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

    it("should support reductions") {
      val unreduced = transform("html5.html")(p => Unit)
      val reduced = transform("html5.html") { page => import page.$
        $("section").reduce()
      }
      val unreducedSections = (unreduced \\ "section").size
      val reducedSections = (reduced \\ "section").size

      unreducedSections should be > (1)
      reducedSections should equal (1)
    }

    it("should support foreach-integrated reductions") {
      val labels = List("One", "Ring", "To", "Rule", "Them", "All")
      val numSections = (transform("html5.html")(page => Unit) \\ "section").size
      val doc = transform("html5.html") { page => import page.$
        $("section").foreachIn(labels, true) { (section, label) =>
          section.replace(true, label)
        }
      }
      val numNewSections = (doc \\ "section").size

      numNewSections should equal (labels.size)
      numNewSections should not equal (numSections)
      numNewSections should not equal (numSections * labels.size)
    }

    it("should implicitly switch context to the duplicated item during foreach when inside another Selectable") {
      val label = "uiuiuiuiui"
      val page = new org.wandledi.scala.SelectableImpl(new Scroll) {
        $("#Sidebar").foreachIn(label :: Nil) { (section, txt) =>
          $.setAttribute("style", "foobar")
          $("div").replace(true, txt)
          // if the context isn't switched implictly, *all* divs in the document will be affected
          // note: Selectable used to switch context provided by SelectableImpl (Selectable)
        }
      }
      val doc = XML.loadString(wandle("html5.html", page.getScroll).get);
      val sideDivs = (doc \\ "section").filter(_.check("id" -> "Sidebar")) \ "div"

      (doc \\ "div").foldLeft(true)((all, div) =>
        all && (div.text.trim == label)) should be (false) // not all divs should've been affected
      (doc \\ "section").filter(_.check("id" -> "Sidebar")).foreach(sec =>
        sec.attribute("style").map(_.text).mkString should equal ("foobar"))
      sideDivs.forall(div => div.text.trim == label) should be (true)
    }

    it("should feature a working reducing foreach with implicit context switches") {
      val labels = List("uiuiuiuiui", "ieieieieie")
      val page = new org.wandledi.scala.SelectableImpl(new Scroll) {
        $("section").foreachIn(labels, reduceBefore = true) { (section, txt) =>
          $("div").replace(true, txt)
          // if the context isn't switched implictly, *all* divs in the document will be affected
          // note: Selectable used to switch context provided by SelectableImpl (Selectable)
        }
      }
      val doc = XML.loadString(wandle("html5.html", page.getScroll).get)
      val secDivs = doc \\ "section" \ "div"

      (doc \\ "div").foldLeft(true)((all, div) =>
        all && labels.contains(div.text.trim)) should be (false) // not all divs should've been affected
      secDivs.forall(div => labels contains div.text.trim) should be (true)
      secDivs should have size (labels.size)
    }

    it("should accept xml.NodeSeqs as insertions and as replacements") {
      val doc = transform("test.xhtml") { page => import page._
        $("body").insert(false, <pre id="insertion">Quid Quo Pro</pre>)
        $(".info").replace(true, <p>Quid Quo Pro</p>)
      }
      val pre = doc \\ "pre"
      pre.size should be (1)
      pre(0).text should equal ("Quid Quo Pro")
      val p = doc \\ "p"
      p.size should be (1)
      p(0).text should equal ("Quid Quo Pro")
    }
  }

  describe("org.wandledi.spells.TextTransformation") {
    it("should support index-based 'spot' insertion") {
      val insertions = Array("Hans", "FunFilms", "Airplane!")
      val doc = transform("strings.xhtml") { page =>
        val ttf = new TextTransformation(insertions)
        page.at("#parenthesis").get(".text").cast(ttf)
      }
      val div = (doc \\ "div").filter(attribute("id" -> "parenthesis"))
      div should have size (1)
      val text = (div \ "p")(0).text
      text should not include ("(")
      text should not include (")")
      insertions.foreach(text should include (_))
    }
    it("should support regex-based 'spot' insertion") {
      val insertions = Map("Mar.+" -> "Heinz", "(T|t)he [a-zA-z]+.*" -> "Vertigo", "Spam.Free" -> "SpareTV")
      val doc = transform("strings.xhtml") { page =>
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
      val doc = transform("strings.xhtml") { page =>
        val ttf = new TextTransformation(insertions.map(m =>
            new SpotMapping(m._1, Array(m._2): _*)).toArray: _*)
        page.at("#parenthesis").get(".text").cast(ttf)
      }
      val div = (doc \\ "div").filter(attribute("id" -> "parenthesis"))
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
      val div = (doc \\ "div").filter(attribute("id" -> "parenthesis"))
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
      val user = spans.find(attribute("id" -> "user")).getOrElse(fail())
      val flavour = spans.find(attribute("id" -> "flavour")).getOrElse(fail())
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
      val doc = transform("test.xhtml") { implicit page => import page._
        $("title").includeFile("strings.xhtml") {
          $(Nil).extract("head")
          $("head").truncate(1)
          $("meta").hide()
        }
      }
      val title = doc \\ "title"
      val head = doc \\ "head"
      val bodyInHead = head \ "body"

      head should have size (1)
      title should have size (1)
      title(0).text should include ("String Insertion Test")
      bodyInHead should be ('empty)
    }
  }

  describe("Inclusion") {
    it("should correctly work with Pages through the Element API") {
      val incl = new Page("noText.xhtml") {
        $(Nil).extract("title")
      }
      val doc = transform("test.xhtml") { page => import page._
        $("html").includeFile(incl)
      }

      doc should have size (1)
      doc.head.text should equal ("Elements with no text")
      doc.head.label should equal ("title")
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
    val wandler = if (file.endsWith(".xhtml")) Wandler.forXHTML else Wandler.forHTML
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
