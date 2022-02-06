package org.arrnaux
package part1recap

object CatsTypeClasses {

  /*
  - applicative
  - functor
  - flatMap
  - monad
  - apply
  - applicativeError/monadError
  - traverse
  */

  /**
   * 1. Functor
   * Describes a data type which is _map-able_.
   */
  trait MyFunctor[F[_]] {
    def map[A, B](initialValue: F[A])(f: (A => B)): F[B]
  }

  import cats.Functor
  import cats.instances.list.*

  val listFunctor = Functor[List]

  // generalizable "mapping" APIs
  def increment[F[_]](container: F[Int])(using functor: Functor[F]): F[Int] =
    functor.map(container)(_ + 1)

  import cats.syntax.functor.*

  // ????
  def increment_v2[F[_] : Functor](container: F[Int]): F[Int] =
    container.map(_ + 1)

  /**
   * 2. Applicative
   * the ability to "wrap" types
   */
  trait MyApplicative[F[_]] extends MyFunctor[F] {
    def pure[A](value: A): F[A]
  }

  import cats.Applicative

  val applicativeList = Applicative[List]
  val aSimpleList: List[Int] = applicativeList.pure(43)

  import cats.syntax.applicative.* // import the pure extension method

  val aSimpleList_v2 = 43.pure[List]

  /**
   * 3. flatMap
   * Has the capability of chaining (multiple) computations
   */
  trait MyFlatMap[F[_]] extends MyFunctor[F] {
    def flatMap[A, B](container: F[A])(f: A => F[B]): F[B]
  }

  import cats.FlatMap

  val flatMapList = FlatMap[List]

  import cats.syntax.flatMap.* // flatMap extension method

  def crossProduct[F[_] : FlatMap, A, B](fa: F[A], fb: F[B]): F[(A, B)] =
    fa.flatMap(a => fb.map(b => (a, b)))

  /**
   * Monad
   * It's a combination of applicative + flatMap
   */

  trait MyMonad[F[_]] extends MyApplicative[F] with MyFlatMap[F] {
    override def map[A, B](initialValue: F[A])(f: A => B): F[B] =
      flatMap(initialValue)(a => pure(f(a)))
  }

  import cats.Monad

  val monadList = Monad[List]

  def crossProduct_v2[F[_] : Monad, A, B](fa: F[A], fb: F[B]): F[(A, B)] =
    for {
      a <- fa
      b <- fb
    } yield (a, b)

    /*
              Functor -> FlatMap -->
                  \                 \
                     Applicative    -> Monad
    */

    trait MyApplicativeError[F[_], E] extends MyApplicative[F] {
      def raiseError[A](e: E): F[A]
    }

    import cats.ApplicativeError
    type ErrorOr[A] = Either[String, A]
    val applicativeErrorEither = ApplicativeError[ErrorOr, String]
    val desirableValue: ErrorOr[Int] = applicativeErrorEither.pure(42)
    val failedValue: ErrorOr[Int] = applicativeErrorEither.raiseError("Something failed")

    import cats.syntax.applicativeError.* // raiseError extension method
    val failedValue_v2: ErrorOr[Int] = "Something failed".raiseError[ErrorOr, Int]
  // undesirable value VS desirable value

  def main(args: Array[String]): Unit = {

  }
}
