package org.arrnaux
package part2effects

import cats.effect.IO

import scala.util.{Failure, Success, Try}

object IOErrorHandling {

  // IO: pure, delay, defer
  // create failed effects
  val aFailedCompute: IO[Int] = IO.delay(throw new RuntimeException("A failuuuuuure"))
  val aFailure: IO[Int] = IO.raiseError(new RuntimeException("a proper fail"))

  // handle exceptions
  val dealWithIt = aFailure.handleErrorWith {
    case _: RuntimeException => IO.delay(println("I'm still here"))
    // add more cases
  }

  // turn into an Either
  val effectAsEither: IO[Either[Throwable, Int]] = aFailure.attempt

  // redeem: transform the failure and the success in one go
  val resultAsString: IO[String] = aFailure.redeem(ex => s"Fail: $ex", value => s"SUCCESS: $value")

  // redeemWith
  val resultAsEffect: IO[Unit] = aFailure.redeemWith(ex => IO(println(s"FAIL: $ex")), value => IO(println(s"SUCCESS: $value")))

  /**
   * Exercises
   */

  // 1 - construct potentially failed IOs from standard data types (Option, Try, Either)
  def option2IO[A](option: Option[A])(ifEmpty: Throwable): IO[A] = option match {
    case Some(value) => IO.pure(value)
    case None => IO.raiseError(ifEmpty)
  }

  def try2IO[A](aTry: Try[A]): IO[A] = aTry match {
    case Success(value) => IO.pure(value)
    case Failure(exception) => IO.raiseError(exception)
  }

  def either2IO[A](anEither: Either[Throwable, A]): IO[A] = anEither match {
    case Left(value) => IO.raiseError(value)
    case Right(value) => IO(value)
  }

  /**
   * We also have #fromTry, #fromEither, #fromFuture, #fromOption, #fromCompletableFuture on IO
   */

  // 2 - create 2 additional methods: - handleError, handleErrorWith
  def handleIOError[A](io: IO[A])(handler: Throwable => A): IO[A] =
    io.redeem(handler, identity)

  def handleIOErrorWith[A](io: IO[A])(handler: Throwable => IO[A]): IO[A] =
    io.redeemWith(handler, IO.pure)

  def main(args: Array[String]): Unit =
    import cats.effect.unsafe.implicits.global
    //    aFailedCompute.unsafeRunSync()
    //    aFailure.unsafeRunSync()
    //    dealWithIt.unsafeRunSync()
    //    println(resultAsString.unsafeRunSync())
    resultAsEffect.unsafeRunSync()
}
