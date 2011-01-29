package wandledi.scala.test

import java.io.File
import java.io.FileReader
import java.io.IOException
import java.io.StringReader
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import org.scalatest.Spec
import org.scalatest.matchers.ShouldMatchers
import org.w3c.dom.Document
import org.wandledi.Scroll
import org.wandledi.Wandler
import org.wandledi.scala.Selectable
import org.wandledi.CssSelector
import org.xml.sax.InputSource
import scala.xml.NodeSeq
import scala.xml.XML

class Semantics extends Spec with ShouldMatchers {
  val testFileDirectory = "core/src/test/java/wandledi/test/"
  def transform(file: String, debug: Boolean)(magic: (Selectable) => Unit): NodeSeq = {
    val page = new Selectable(new Scroll)
    magic(page)
    val result = wandle(file, page.getScroll)
    result should be ('defined)
    if (debug) println(result.get)
    XML.loadString(result.get)
  }
  def transform(file: String)(magic: (Selectable) => Unit): NodeSeq = transform(file, false)(magic)

  describe("ScalaSelectable") {
    it("should support context sensetive selection ($)") {
      val doc = transform("test.xhtml", debug=true) { page => import page._
        $("div").setAttribute("foo", "bar")
        $$(".info") {
          println(page.toString + " vs " + $.toString)
          $("div").setAttribute("color", "red") // should only apply to the nested div
        }
      }
      val divs = doc \\ "div"
      divs.foreach(_.attribute("foo").get.head.text should equal ("bar"))
      val redDivs = divs.filter(div => div.attribute("color").isDefined)
      redDivs should have size (1)
      redDivs.head.text should equal ("Repeat: ")
    }
  }
  
  def wandle(file: String, scroll: Scroll): Option[String] = {
    val wandler = new Wandler
    val output = new StringWriter
    var input: FileReader = null
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
