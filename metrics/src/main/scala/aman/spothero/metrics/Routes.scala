package aman.spothero.metrics

import cats.effect.Sync
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

object Routes {
  def metrics[F[_]: Sync](M: Metrics[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "metrics" =>
        for {
          metrics <- M.get
          resp <- Ok(metrics)
        } yield resp
    }
  }
}
