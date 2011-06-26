package wandledi.test

import org.scalatest.testng.TestNGSuite
import org.wandledi.Wandler

class HTML5Wrapper extends HTML5 with TestNGSuite

// test everything again with HtmlParser just to be sure
class HtmlSpellExperiment extends SpellExperiment(Wandler.forHTML) with TestNGSuite