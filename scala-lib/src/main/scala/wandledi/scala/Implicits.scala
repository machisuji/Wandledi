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
   * evaluated if the conditional holds true (or false respectively).
   */
  implicit def trailingConditionals[T](any: => T) = new {
    def provided(expr: Boolean) = if (expr) Some(any) else None
    def unless(expr: Boolean) = if (!expr) Some(any) else None
  }
  
  /**Introduces the elvis operator as known from Groovy.
   * So instead of
   * <br/>
   * val id = if (param("id") != null) param("id") else 42
   * <br/>
   * you can now write
   * <br/>
   * val id = param("id") ?: 42
   * <br/>
   * The right hand operand will only be evaluated if the
   * left hand operand is actually null.
   * Meaning the following will only print "null":
   * <br/>
   * null ?: {println("null")}
   * "fo" ?: {println("foo")}
   */
  implicit def elvisOperator[T](alt: => T) = new {
    def ?:[S >: T](checked: S) = if (checked != null) checked else alt
  }
}
