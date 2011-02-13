package org.wandledi.scala

trait TextContent {
  /**Inserts values at marked spots within this text.
   * A spot is any part of the text that is enclosed in parenthesis.
   * Example:
   *
   * <pre>    &lt;p id="welcome"&gt;Welcome, dear (Bobo)!&lt;/p&gt;</pre>
   *
   * Which would be transformed like this:
   *
   * <pre>    $("#welcome").text.insert(user.name)</pre>
   *
   * This might become a problem if there is a regular part enclosed with
   * parenthesis that is not supposed to be replaced with dynamic content.
   * In this case either use the index-mapped version of #insert or pass
   * null at the respective index.
   *
   * @param values The Strings to be inserted.
   */
  def insert(valuesHead: String, valuesTail: String*): Unit

  /**Inserts values at the marked spots within this text.
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
  def insert(values: (Int, String)*): Unit

  /**Inserts values at the marked spots within this text.
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
  def insert(valuesHead: (String, String), valuesTail: (String, String)*): Unit
}
