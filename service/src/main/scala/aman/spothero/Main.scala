package aman.spothero

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._

object SpotheroWeb extends IOApp {
  def run(args: List[String]) =
    SpotheroServer.stream[IO].compile.drain.as(ExitCode.Success)
}
