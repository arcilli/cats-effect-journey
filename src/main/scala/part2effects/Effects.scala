package org.arrnaux
package part2effects

import scala.concurrent.Future

object Effects {
  // pure functional programming
  // substitution
  def combine(a: Int, b: Int): Int = a + b

  val five = combine(2, 3)
  val five_v2 = 2 + 3
  val five_v3 = 5

  // referential transparency = can replace an expression with its value
  // as many times as we want without changing behavior


  // example of impure function: print to the console
  val printSomething: Unit = println("Cats effect")
  val printSomething_v2: Unit = ()

  // printSomething & printSomething_v2 have the same return value, but they're not the same. The behavior of the program has changed.

  // example: change a variable
  var anInt = 0
  val changingVar: Unit = (anInt += 1)
  val changingVar_v2: Unit = () // not the same thing -> the previous one modifies the variable, the second one not, but they have the same return value

  /*!!! side effects are inevitable for useful programs

  we need the concept of _effect_:
    - a concept that bridges our necessity to produce side-effects with the desire to write purely functional code
    - it's a datatype that embodies the concept of a side-effect/any sort of computation we might need in our code

    Effect types
    - properties:
      - tell what kind of side-effect will be produced only by looking at the type signature
      - type signature describes the KIND OF CALCULATION that will be performed
      - type signature describes the VALUE that will be calculated
      - when side effects are needed, effect construction should be separated from the effect execution
  */

  /*
  example: Option is an effect type
    - describes a possibly absent value
    - computes a value of type A, if it exists
    - side effects are not needed
  */

  /*
    example: Future is NOT a good effect type
      - describes an asynchronous computation that will be performed at some point in the future
      - computes a value of type A, if it's successful
      - side effect is required (allocating/scheduling a thread is needed), execution is NOT separated from construction
  */

  import scala.concurrent.ExecutionContext.Implicits.global

  val aFuture: Future[Int] = Future(42)


  /*
    example: MyIO data type from the Monads lesson - it IS an effect type
      - describes any computation that might produce side effects
      - calculates a value of type A, if it's successful
      - side effects are required for the evaluation of () => A
        - YES, the creation of MyIO does not produce the side effects on construction
  */

  case class MyIO[A](unsafeRun: () => A) {
    def map[B](f: A => B): MyIO[B] =
      MyIO(() => f(unsafeRun()))

    def flatMap[B](f: A => MyIO[B]): MyIO[B] =
      MyIO(() => f(unsafeRun()).unsafeRun())
  }

  val anIO: MyIO[Int] = MyIO(() => {
    println("I'm writing something...")
    42
  })


  val anOption: Option[Int] = Option(42)


  def main(args: Array[String]): Unit = {
    anIO.unsafeRun()
  }

}
