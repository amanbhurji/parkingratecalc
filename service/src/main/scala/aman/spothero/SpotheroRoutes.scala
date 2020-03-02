package aman.spothero

import aman.spothero.api.{ParkingCalculator, ParkingPeriod, RatesManager}
import aman.spothero.api.models.Rates
import aman.spothero.api.models.Rates._
import cats.effect.Sync
import cats.implicits._
import io.prometheus.client.{CollectorRegistry, Counter}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

object SpotheroRoutes {

  def parkingCalculatorRoutes[F[_]](
      P: ParkingCalculator[F],
      registry: CollectorRegistry
  )(implicit F: Sync[F]): HttpRoutes[F] = {
    val successCounter =
      Counter.build("pr_success", "successfully calculated rate").register(registry)
    val failureCounter = Counter.build("pr_failed", "failed to calculate rate").register(registry)

    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case request @ GET -> Root / "calculate" =>
        for {
          parkingPeriod <- request.as[ParkingPeriod]
          maybeCost <- P.price(parkingPeriod)
          resp <- maybeCost.fold(
            { l =>
              F.delay(failureCounter.inc()) *> Ok(l)
            }, { r =>
              F.delay(successCounter.inc()) *> Ok(r)
            }
          )
        } yield resp
    }
  }

  def ratesManagerRoutes[F[_]: Sync](R: RatesManager[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case request @ POST -> Root / "rates" =>
        for {
          rates <- request.as[Rates]
          _ <- R.updateRates(rates)
          resp <- Ok()
        } yield resp
    }
  }

//  implicit val isoZonedDateTimeQueryParamDecoder: QueryParamDecoder[ZonedDateTime] =
//    zonedDateTimeQueryParamDecoder(DateTimeFormatter.ISO_ZONED_DATE_TIME)
//  object StartTimeQueryParamMatcher extends QueryParamDecoderMatcher[ZonedDateTime]("start")
//  object EndTimeQueryParamMatcher extends QueryParamDecoderMatcher[ZonedDateTime]("end")
//
//  def zonedDateTimeQueryParamDecoder(
//      formatter: DateTimeFormatter
//  ): QueryParamDecoder[ZonedDateTime] =
//    new QueryParamDecoder[ZonedDateTime] {
//      override def decode(value: QueryParameterValue): ValidatedNel[ParseFailure, ZonedDateTime] = {
//        println(s"ATTEMPTING TO DECODE - ${value.value}")
//        Validated
//          .catchOnly[DateTimeParseException] {
//            val x: TemporalAccessor = formatter.parse(value.value)
//            ZonedDateTime.from(x)
//          }
//          .leftMap { e =>
//            ParseFailure(s"Failed to decode value: ${value.value} as ZonedDateTime", e.getMessage)
//          }
//          .toValidatedNel
//      }
//    }
}
