package aman.spothero

import aman.spothero.api.{ParkingCalculator, RatesManager}
import aman.spothero.persistence.RatesRepository
import cats.effect.{ConcurrentEffect, Timer}
import cats.implicits._
import fs2.Stream
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger

object SpotheroServer {

  def stream[F[_]: ConcurrentEffect](implicit T: Timer[F]): Stream[F, Nothing] = {
    for {
      _ <- Stream.emit(())
      rateRepositoryAlg = RatesRepository.impl
      ratesManagerAlg = RatesManager.impl[F](rateRepositoryAlg)
      parkingCalculatorAlg = ParkingCalculator.impl[F](rateRepositoryAlg)

      httpApp = (
        SpotheroRoutes.ratesManagerRoutes[F](ratesManagerAlg) <+>
          SpotheroRoutes.parkingCalculatorRoutes[F](parkingCalculatorAlg)
      ).orNotFound

      // With Middlewares in place
      finalHttpApp = Logger.httpApp(true, false)(httpApp)

      exitCode <- BlazeServerBuilder[F]
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain
}
