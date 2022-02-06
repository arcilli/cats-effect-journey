package org.arrnaux
package part1recap

object ContextualAbstractionScala3 {

  // given/using combo
  def increment(x: Int)(using amount: Int): Int = x + amount

  given defaultAmount: Int = 10

  val twelve = increment(2)

  def multiply(x: Int)(using amount: Int): Int = x * amount

  val aHundred = multiply(10) // defaultAmount is passed automatically

  // more complex use case
  trait Combiner[A] {
    def combine(x: A, y: A): A

    def empty: A
  }

  def combineAll[A](values: List[A])(using combiner: Combiner[A]): A = {
    values.foldLeft(combiner.empty)(combiner.combine)
  }

  given intCombiner: Combiner[Int] with {
    override def combine(x: Int, y: Int): Int = x + y

    override def empty: Int = 0
  }

  val numbers = (1 to 10).toList
  val sum10 = combineAll(numbers) // intCombiner is passed automatically

  //  combineAll(List("Cats", "Scala")) will not work because there is no "given" for Combiner[String]


  // synthesize given instances
  given optionCombiner[T] (using combiner: Combiner[T]): Combiner[Option[T]] with {
    override def combine(x: Option[T], y: Option[T]): Option[T] = for {
      vX <- x
      vY <- y
    } yield combiner.combine(vX, vY)

    override def empty: Option[T] = Some(combiner.empty)
  }

  val sumOptions: Option[Int] = combineAll(List(Some(1), None, Some(2)))

  // extension methods
  case class Person(name: String) {
    def greet(): String = "Hi, my name is $name"
  }

  extension (name: String)
    def greet(): String = Person(name).greet()

  val alicesGreeting: String = "Alice".greet()

  // generic extension
  extension[T] (list: List[T])
    def reduceAll(using combiner: Combiner[T]): T =
      list.foldLeft(combiner.empty)(combiner.combine)

  val sum10_v2 = numbers.reduceAll


  object TypeClassScala3 {
    case class Person(name: String, age: Int)
    // type classes

    // part 1 - Type class definition
    trait JsonSerializer[T] {
      def toJson(value: T): String
    }

    // part 2 - define type class instances
    given stringSerializer: JsonSerializer[String] with {
      override def toJson(value: String): String = "\"" + value + "\""
    }

    given intSerializer: JsonSerializer[Int] with {
      override def toJson(value: Int): String = value.toString
    }

    given personSerializer: JsonSerializer[Person] with {
      override def toJson(value: Person): String =
        s"""
           |{"name": "${value.name}",
           |"age": "${value.age}}"
           |""".stripMargin.trim
    }

    // part 3 - create user-facing API
    def convert2Json[T](value: T)(using serializer: JsonSerializer[T]): String =
      serializer.toJson(value
      )

    def convertList2Json[T](list: List[T])(using serializer: JsonSerializer[T]): String =
      list.map(value => serializer.toJson(value)).mkString("[", ",", "]")

    // part 4 - extension methods just for the types we support
    extension[T] (value: T)
      def toJson(using serializer: JsonSerializer[T]): String =
        serializer.toJson(value)
  }

  def main(array: Array[String]): Unit = {
    print(TypeClassScala3.convertList2Json(List(TypeClassScala3.Person("Alice", 23), TypeClassScala3.Person("Bob", 46))))

    val bob = TypeClassScala3.Person("Boob", 49 )
    println(bob.toJson)
  }

}
