package org.arrnaux
package part2effects

import cats.effect.IO

import scala.io.StdIn

object IOIntroduction {

  // IO
  val ourFirstIO: IO[Int] = IO.pure(44) // argument that should not have side effects
  val aDelayedIO: IO[Int] = IO.delay({ // argument is passed by name
    println("I'm producing an integer")
    56
  })

  val shouldNotDoThis: IO[Int] = IO.pure {
    println("I'm producing an integer")
    52
  }

  val aDelayedIO_v2: IO[Int] = IO { // apply == delay
    println("I'm producing an integer")
    56
  }

  // map, flatMap
  val improvedMeaningOfLife: IO[Int] = ourFirstIO.map(_ * 2)
  val printedMeaningOfLife: IO[Unit] = ourFirstIO.flatMap(mol => IO.delay(println(mol)))

  def smallProgram(): IO[Unit] = for {
    line1 <- IO(StdIn.readLine())
    line2 <- IO(StdIn.readLine())
    _ <- IO.delay(println(line1 + line2))
  } yield ()

  // mapN - combine IO effects as tuples
  // it's an extension method

  import cats.syntax.apply.*

  val combinedMeaningOfLife: IO[Int] = (ourFirstIO, improvedMeaningOfLife).mapN(_ + _)

  def smallProgram_v2(): IO[Unit] =
    (IO(StdIn.readLine()), IO(StdIn.readLine())).mapN(_ + _).map(println)


  /**
   * Exercises
   *
   */


  // 1 - sequence two IOs and take the result of the LAST one
  // hint: use flatMap

  import cats.effect.unsafe.implicits.global

  def sequenceTakeLast[A, B](ioa: IO[A], iob: IO[B]): IO[B] =
    ioa.flatMap(_ => iob)

  def sequenceTakeLast_v2[A, B](ioa: IO[A], iob: IO[B]): IO[B] =
    ioa *> iob // __andThen__ operator

  def sequenceTakeLast_v3[A, B](ioa: IO[A], iob: IO[B]): IO[B] =
    ioa >> iob // __andThen__ with by-name call, the second argument evaluation is lazy

  // 2 - sequence two IOs and take the result of the FIRST one
  // hint: use flatMap
  def sequenceTakeFirst[A, B](ioa: IO[A], iob: IO[B]): IO[A] =
    ioa.flatMap(x => iob.map(_ => x)) // you could use also for-comprehension

  def sequenceTakeFirst_v2[A, B](ioa: IO[A], iob: IO[B]): IO[A] =
    ioa <* iob // ioa __before__ iob

  // 3 - repeat an IO effect forever
  // hint: use flatMap + recursion
  def forever[A](io: IO[A]): IO[A] =
    io.flatMap(_ => forever(io))

  def forever_v2[A](io: IO[A]): IO[A] =
    io >> forever_v2(io)

  def forever_v3[A](io: IO[A]): IO[A] =
    io *> forever_v3(io)

  def forever_v4[A](io: IO[A]): IO[A] =
    io.foreverM // with tail recursion

  // 4 - convert an IO to a different type
  // hint - use a hint
  def convert[A, B](ioa: IO[A], value: B): IO[B] =
    val a: IO[B] = ioa.map(_ => value)
    return a

  def convert_v2[A, B](ioa: IO[A], value: B): IO[B] =
    ioa.as(value) //same

  // 5 - discard value inside an IO, just return Unit
  def asUnit[A](ioa: IO[A]): IO[Unit] =
    ioa.map(_ => IO.unit)

  def asUnit_v2[A](ioa: IO[A]): IO[Unit] =
    ioa.as(()) // discourage - don't use this

  def asUnit_v1_v2[A](ioa: IO[A]): IO[Unit] =
    ioa.map(_ => ()) // discourage - don't use this

  def asUnit_v3[A](ioa: IO[A]): IO[Unit] =
    ioa.void

  // 6 - fix stack recursion
  def sum(n: Int): Int =
    if (n <= 0) 0
    else n + sum(n - 1)

  def sumIO(n: Int): IO[Int] =
    if (n <= 0) IO(0)
    else for {
      lastNumber <- IO(n)
      prevSum <- sumIO(n - 1)
    } yield prevSum + lastNumber


  // 7 - write a fibonacci IO that does NOT crash on recursion
  // hints: use recursion, ignore exponential complexity, use flatMap heavily
  def fibonacci(n: Int): IO[BigInt] =
    if (n < 2) IO(1)
    else for {
//      last <- IO(fibonacci(n - 1)) // IO[IO[BigInt]]
      last <- IO(fibonacci((n-1))).flatMap(x => x)
      prev <- IO(fibonacci(n - 2)).flatMap(x => x)
    } yield last + prev

  def main(args: Array[String]): Unit = {
    import cats.effect.unsafe.implicits.global // "platform"

    // "end of the world"s
    //    println(smallProgram_v2().unsafeRunSync())

    //    forever(IO(println("forever"))).unsafeRunSync()
    //    forever_v3(IO { // this will beautiful crash
    //      println("forever!")
    //      Thread.sleep(100)
    //    }).unsafeRunSync()
  }
}
