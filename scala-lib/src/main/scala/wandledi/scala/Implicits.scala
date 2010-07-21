package wandledi.scala

object Implicits {

  /**Introduces trailing conditionals.
   * That is instead of
   * <br/>
   * if (debug) println("DEBUG: foobar")
   * <br/>
   * you can now write
   * <br/>
   * println("DEBUG: foobar") provided isDebugMode
   * <br/>or</br>
   * println("DEBUG: baz") unless isProductionMode
   * <br/>
   * The expression before the trailing conditionals will only be
   * executed if the conditional holds true (or false respectively).
   */
  implicit def trailingConditionals[T](any: => T) = new {
    def provided(expr: Boolean) = if (expr) Some(any) else None
    def unless(expr: Boolean) = if (!expr) Some(any) else None
  }
}
