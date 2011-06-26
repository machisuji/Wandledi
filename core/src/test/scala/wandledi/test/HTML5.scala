package wandledi.test

import org.scalatest.testng.TestNGSuite
import org.wandledi.Wandler

class HTML5Wrapper extends HTML5 with TestNGSuite

class HtmlSpellExperiment extends SpellExperiment(Wandler.forHTML) with TestNGSuite