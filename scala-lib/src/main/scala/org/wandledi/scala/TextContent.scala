package org.wandledi.scala

import org.wandledi.spells.SpotMapping
import org.wandledi.spells.StringTransformation

/**
 * TextContent provides transformations for an Element's text content, all of which are
 * different instances of TextTransformation.
 *
 * @see org.wandledi.spells.TextTransformation
 */
trait TextContent extends org.wandledi.TextContent {

  /**
   * Transforms an Element's text.
   *
   * @param stringTransformation A function taking the original text and returning the new one to be used instead.
   */
  def transform(stringTransformation: String => String) {
    val st = new StringTransformation {
      def transform(input: String) = stringTransformation(input)
    }
    transform(st)
  }

  /**
   * Selects parts of the text according to a regex and transforms those parts.
   *
   * @param regex Regex to match the parts to be transformed.
   * @param stringTransformation A function transforming the text.
   */
  def transform(regex: String)(stringTransformation: String => String) {
    val st = new StringTransformation {
      def transform(input: String) = stringTransformation(input)
    }
    transform(regex, st)
  }

  /**
   * Inserts values at the marked spots within this text.
   * A spot is any part of the text that is enclosed with parenthesis.
   * The values are mapped the respective spots via an index.
   * Example:
   *
   * <pre>    &lt;p id="welcome"&gt;Welcome to (your profile) dear (Bobo)!&lt;/&gt;</pre>
   *
   * Which would be transformed like this:
   *
   * <pre>    $("#welcome").text.insert(1 -> user.name, 0 -> msg('currentPage))</pre>
   *
   * @param values Strings to be inserted mapped by the target index.
   */
  def insert(values: (Int, String)*) {
    val orderedValues = Array.ofDim[String](values.size)
    for ((index, value) <- values) {
      orderedValues(index) = value
    }
    insert(orderedValues: _*)
  }

  /**
   * Inserts values at the marked spots within this text.
   * A spot is any part of the text that is enclosed with parenthesis.
   * The values are mapped to the respective spots via a name.
   * Example:
   *
   * <pre>    &lt;p id="welcome"&gt;Welcome to (your profile) dear (Bobo)!&lt;/&gt;</pre>
   *
   * Which would be transformed like this:
   *
   * <pre>    $("#welcome").text.insert("*profile" -> msg('currentPage), "Bobo" -> user.name)</pre>
   *
   * Names can begin (or end) with wildcards, providing a 'endsWith' and 'startsWith' match
   * besides the full name match.
   */
  def insert(valuesHead: (String, String), valuesTail: (String, String)*) {
    insert(false, (valuesHead +: valuesTail): _*)
  }

  /**
   * Same as insert((String, String)+) though regex are used to identify the spots
   * instead of (slightly enhanced) names.
   */
  def insertR(values: (String, String)*) {
    insert(true, values: _*)
  }

  /**Same as insert((String, String)+) though with the option
   * to use regex to match the names.
   */
  def insert(regex: Boolean, values: (String, String)*) {
    val mappings = values.map(m => new SpotMapping(m._1, regex, m._2))
    insert(mappings: _*)
  }
}
