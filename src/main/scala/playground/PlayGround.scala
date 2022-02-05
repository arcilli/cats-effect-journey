package org.arrnaux
package playground

import cats.effect.{IO, IOApp}

object PlayGround extends IOApp.Simple {
  override def run: IO[Unit] =
    IO.println("Fooling around with some cats 3 effect...")
}
