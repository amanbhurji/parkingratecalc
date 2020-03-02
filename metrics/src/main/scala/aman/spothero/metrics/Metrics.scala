package aman.spothero.metrics

import java.io.{StringWriter, Writer}

import aman.spothero.metrics.Metrics.MetricsInfo
import cats.effect.Sync
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.common.TextFormat
import org.http4s._
import org.http4s.headers.`Content-Type`

trait Metrics[F[_]] {
  def get: F[MetricsInfo]
}

object Metrics {
  def apply[F[_]](implicit ev: Metrics[F]): Metrics[F] = ev

  final case class MetricsInfo(value: String) extends AnyVal
  object MetricsInfo {
    implicit def metricsInfoEntityEncoder[F[_]]: EntityEncoder[F, MetricsInfo] =
      EntityEncoder
        .stringEncoder[F]
        .contramap[MetricsInfo](_.value)
        .withContentType(
          `Content-Type`(
            MediaType
              .parse(TextFormat.CONTENT_TYPE_004)
              .getOrElse(MediaType.text.plain)
          )
        )
  }

  def impl[F[_]](
      registry: CollectorRegistry
  )(
      implicit F: Sync[F]
  ): Metrics[F] = new Metrics[F] {
    def get: F[MetricsInfo] = F.delay {
      val writer: Writer = new StringWriter()
      val metrics = registry.metricFamilySamples()
      TextFormat.write004(writer, metrics)
      MetricsInfo(writer.toString)
    }
  }
}
