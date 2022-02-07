package org.arrnaux
package part2effects

import scala.concurrent.Future
import scala.io.StdIn

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

  /**
   * Exercises
   * 1. An IO which returns the current time of the system
   * 2. An IO which measures the duration of a computation (hint: use ex.1)
   * 3. An IO which prints something to the console
   * 4. An IO which reads a line (a string) from std input.
   */

  // 1.
  val clock: MyIO[Long] = MyIO(() => System.currentTimeMillis())

  // 2.
  def measure[A](computation: MyIO[A]): MyIO[Long] = {
    for {
      startTime <- clock
      _ <- computation
      endTime <- clock
    } yield endTime - startTime
  }
  /*
    clock.flatMap(startTime => computation.flatMap(_ => computation.map(finishTime => finishTime - startTime))

    computation.map(finishTime => finishTime - startTime) = MyIO( () =>
      computation.unsafeRun() - startTime = System.currentTimesMilis - startTime

  =>
    clock.flatMap(startTime => computation.flatMap(_ => MyIO( () => System.currentTimeMilis() - startTime)

    computation.flatMap(lambda) = MyIO ( () => lambda(computation).unsafeRun())
                                = MyIO ( () => MyIO( () => System.currentTimeMilis() - startTime)).unsafeRun())
                                = MyIO ( () => System.currentTimeMilis_after_computation() - startTime)


    => computation.flatMap(startTime => MyIO( () => System.currentTimeMilis_after_computation() - startTime))
  = MyIO( () => lambda(computation.unsafeRun)).unsafeRun())
  = MyIO(() => lambda(System.currentTimeMilis_after_computation - System.currentTimeMilis()).unsafeRun())
  = MyIO(() => System.currentTimeMilis_after_computation() - System.currentTimeMilis_at_start())
  */

  def testTimeIO(): Unit = {
    val test = measure(MyIO(() => Thread.sleep(1000)))
    println(test.unsafeRun)
  }

  // 3.
  def putStrLn(line: String): MyIO[Unit] = MyIO(() => println(line))

  // 4.
  val read: MyIO[String] = MyIO(() => StdIn.readLine())


  def testConsole(): Unit = {
    val program: MyIO[Unit] = for {
      line1 <- read
      line2 <- read
      _ <- putStrLn(line1 + line2)
    } yield ()
    program.unsafeRun()
  }


  def main(args: Array[String]): Unit = {
    //    anIO.unsafeRun()
    //    testTimeIO()
    testConsole()
  }

}
