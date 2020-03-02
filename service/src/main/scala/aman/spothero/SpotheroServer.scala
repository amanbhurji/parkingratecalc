package aman.spothero

import aman.spothero.api.{ParkingCalculator, RatesManager}
import aman.spothero.{metrics => asm}
import aman.spothero.persistence.RatesRepository
import cats.effect.{Clock, ConcurrentEffect, ContextShift, Timer}
import cats.implicits._
import fs2.Stream
import org.http4s.{HttpApp, Request}
import org.http4s.implicits._
import org.http4s.metrics.prometheus.Prometheus
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.{Logger, Metrics}

object SpotheroServer {

  def stream[F[_]: ConcurrentEffect: ContextShift](implicit T: Timer[F]): Stream[F, Nothing] = {
    for {
      _ <- Stream.emit(())
      implicit0(clock: Clock[F]) = Clock.create[F]

      // With metrics Middlewares in place
      meteredRoutes <- Stream.resource(
        for {
          registry <- Prometheus.collectorRegistry

          requestMethodClassifier = (r: Request[F]) => Some(r.uri.toString.toLowerCase)
          metricsAlg = asm.Metrics.impl[F](registry)
          metricsRoute = asm.Routes.metrics[F](metricsAlg)

          rateRepositoryAlg = RatesRepository.impl
          ratesManagerAlg = RatesManager.impl[F](rateRepositoryAlg)
          parkingCalculatorAlg = ParkingCalculator.impl[F](rateRepositoryAlg)

          appRoutes = SpotheroRoutes.ratesManagerRoutes[F](ratesManagerAlg) <+>
            SpotheroRoutes.parkingCalculatorRoutes[F](parkingCalculatorAlg, registry)

          metrics <- Prometheus.metricsOps[F](registry, "spothero_server")
        } yield Metrics[F](
          metrics,
          classifierF = requestMethodClassifier
        )(appRoutes <+> metricsRoute)
      )

      httpApp = meteredRoutes.orNotFound

      // With logging Middlewares in place
      finalHttpApp: HttpApp[F] = Logger.httpApp(true, false)(httpApp)

      exitCode <- BlazeServerBuilder[F]
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .serve

    } yield exitCode
  }.drain
}
