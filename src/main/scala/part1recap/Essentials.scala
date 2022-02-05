package org.arrnaux
package part1recap

import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object Essentials {

  // values
  val aBoolean: Boolean = false

  // expressions are EVALUATED to a value.
  // instructions do not evaluate
  val anIfExpression = if (aBoolean) "yes" else "no"

  // instructions VS expressions
  val theUnit = println("Hi")

  // OOP
  class Animal

  class Cat extends Animal

  trait Carnivore {
    def eat(animal: Animal): Unit
  }

  // inheritance model: extend at most 1 class, but inherit from multiple traits

  // Crodocodile extends Animal and MIXES IN Carnivore
  class Crocodile extends Animal with Carnivore {
    override def eat(animal: Animal): Unit = println("Crunch")
  }

  // singleton
  object MySingleton // singleton pattern in one line

  object Carnivore // the companion object of the class Carnivore

  // generics
  class MyList[A]

  // method notation
  val three = 1 + 2
  val anotherTree = 1.+(2)

  // FP
  val incrementer: (Int => Int) = x => x + 1
  val incremented = incrementer(45) // 46

  // higher-order functions
  // map, flatMap, filter
  val processedList = List(1, 2, 3).map(incrementer)

  val aLongerList = List(1, 2, 3).flatMap(x => List(x, x + 1)) // List(1, 2,   2, 3,   4, 5)

  // for comprehensions
  val checkerboard = List(1, 2, 3).flatMap(n => List("a", "b", "c").map(c => (n, c)))
  val anotherCheckerBoard = for {
    n <- List(1, 2, 3)
    c <- List("a", "b", "c", "d")
  } yield (n, c) // equivalent expression for anotherCheckerBoard & checkerboard

  // options & try
  def anOption: Option[Int] = Option(3)

  val doubledOption: Option[Int] = anOption.map(_ * 2)

  val anAttempt = Try(/*something that might throw*/ 342) // it has subclass: Success(342) or Failure
  val aModifiedAttempt: Try[Int] = anAttempt.map(_ + 10)

  // pattern matching
  val anUnknown: Any = 45
  val ordinal = anUnknown match {
    case 1 => "first"
    case 2 => "2nd"
    case _ => "unknown"
  }

  val optionDescription: String = anOption match {
    case Some(value) => s"the option is not empty: $value"
    case None => "option is empty"
  }

  // Futures
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(5))
  val aFuture = Future {
    // some code
    42
  }

  // wait for completion (async)
  aFuture.onComplete {
    case Success(value) => println(s"The async meaning: $value")
    case Failure(exception) => println(s"The async meaning failed: $exception")
  }

  // map a Future
  val anotherFuture = aFuture.map(_ + 1) // Future(43) when it completes


  // partial functions
  val aPartialFunction: PartialFunction[Int, Int] = {
    case 1 => 43
    case 8 => 56
  }

  // some more advanced stuff
  trait HigherKindedType[F[_]]

  trait SequenceChecker[F[_]] {
    def isSequential: Boolean
  }

  val listChecker = new SequenceChecker[List] {
    override def isSequential: Boolean = true
  }

  def main(args: Array[String]): Unit = {

  }
}
