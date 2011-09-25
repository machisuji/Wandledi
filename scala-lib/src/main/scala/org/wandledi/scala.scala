package org.wandledi

import collection.JavaConversions._

/**
 * Provide implicit conversions for Selectors.
 *
 * @author Markus Kahl
 * @version 0.1
 *
 * 25.09.11
 */

package object scala {
  implicit def strToCss(sel: String) = CssSelector valueOf sel
  implicit def seqToPath(path: Seq[String]) = new PathSelector(path.map(new ElementStart(_)))
}