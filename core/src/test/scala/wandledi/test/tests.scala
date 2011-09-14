package wandledi.test

import org.scalatest.testng.TestNGSuite
import org.wandledi.Wandler

class SpellExperimentWrapper extends SpellExperiment with TestNGSuite
class SelectorExperimentWrapper extends SelectorExperiment with TestNGSuite

class HTML5Wrapper extends HTML5 with TestNGSuite
class HtmlSpellExperiment extends SpellExperiment(Wandler.forHTML) with TestNGSuite // test everything again with HtmlParser just to be sure
