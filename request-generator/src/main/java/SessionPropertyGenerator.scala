

class SessionPropertyGenerator(operatorSelector : Selector, providerIdSelector : Selector, platformSelector : Selector, regionSelectors : Map[String, Selector], backOfficeSelector : Map[String, Selector]) extends Iterator[String] {
   override def hasNext: Boolean = true

   override def next(): String = {
      val operator = operatorSelector.next()
      s"$operator-${providerIdSelector.next()}-${platformSelector.next()}-${regionSelectors(operator).next()}-${backOfficeSelector(operator).next()}"
   }
}
