package capture



class TagGroupGenerator(selectors : TagGenerator*) extends Iterator[Seq[(String, String)]] {
   override def hasNext: Boolean = true

   override def next(): Seq[(String, String)] = {
      selectors.map { _.next() }
   }
}
