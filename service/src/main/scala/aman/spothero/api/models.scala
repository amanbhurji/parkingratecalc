package aman.spothero.api

import java.time.ZonedDateTime

import aman.spothero.{core, persistence}
import cats.effect.Sync
import io.circe.generic.JsonCodec
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}

object models {
  // Ideally would be different from the persistence encoding but is the same in this example
  type Rates = persistence.Rates
  object Rates {
    implicit def entityDecoderRates[F[_]: Sync]: EntityDecoder[F, Rates] =
      jsonOf[F, Rates]
  }
}

@JsonCodec(decodeOnly = true)
final case class ParkingPeriod(start: ZonedDateTime, end: ZonedDateTime)
object ParkingPeriod {
  implicit def entityDecoderParkingPeriod[F[_]: Sync]: EntityDecoder[F, ParkingPeriod] =
    jsonOf[F, ParkingPeriod]

  def convert(parkingPeriod: ParkingPeriod): Either[String, core.ParkingPeriod] =
    core.ParkingPeriod(parkingPeriod.start, parkingPeriod.end)
}

@JsonCodec(encodeOnly = true)
case class Cost(value: BigDecimal)
object Cost {
  implicit def costEntityEncoder[F[_]]: EntityEncoder[F, Cost] =
    jsonEncoderOf[F, Cost]
}
