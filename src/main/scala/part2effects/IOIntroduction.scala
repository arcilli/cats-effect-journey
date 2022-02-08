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
  import cats.syntax.apply._
  val combinedMeaningOfLife: IO[Int] = (ourFirstIO, improvedMeaningOfLife).mapN(_ + _)
  def smallProgram_v2(): IO[Unit] =
    (IO(StdIn.readLine()), IO(StdIn.readLine())).mapN(_ + _).map(println)

  def main(args: Array[String]): Unit = {
    import cats.effect.unsafe.implicits.global // "platform"

    // "end of the world"s
    println(smallProgram_v2().unsafeRunSync())
  }
}
