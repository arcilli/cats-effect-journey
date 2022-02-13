package org.arrnaux
package part1recap

object ContextualAbstractionsAndTypeClasses {

  // given/using combo
  def increment(x: Int)(using amount: Int): Int = x + amount

  given defaultAmount: Int = 10

  val twelve = increment(2) // the compiler pass defaultAmount automatically by the compiler

  def multiply(x: Int)(using factor: Int): Int = x * factor

  val aHundred = multiply(10) // defaultAmount is passed automatically

  // a more complex use case
  trait Combiner[A] {
    def combine(x: A, y: A): A

    def empty: A
  }

  def combineAll[A](values: List[A])(using combiner: Combiner[A]): A =
    values.foldLeft(combiner.empty)(combiner.combine)


  given intCombiner: Combiner[Int] with {
    override def combine(x: Int, y: Int) = x + y

    override def empty = 0
  }

  val numbers = (1 to 10).toList
  val sum10 = combineAll(numbers) // intCombiner passed automatically

  // combineAll(List("cats", "Sss")) // this piece will not compile

  // synthesize given instances
  given optionCombiner[T] (using combiner: Combiner[T]): Combiner[Option[T]] with {
    override def combine(x: Option[T], y: Option[T]): Option[T] = for {
      vx <- x
      cy <- y
    } yield combiner.combine(vx, cy)

    val sumOptions: Option[Int] = combineAll(List(Some(1), None, Some(2)))

    override def empty: Option[T] = Some(combiner.empty)
  }

  // extension methods
  case class Person(name: String) {
    def greet(): String = s"Hi, my name is $name"
  }



  // type classes
  def main(args: Array[String]): Unit = {

  }
}
