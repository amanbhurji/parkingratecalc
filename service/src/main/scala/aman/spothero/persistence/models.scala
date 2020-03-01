package aman.spothero.persistence

import java.time.{DayOfWeek, LocalTime, ZoneId}
import java.time.format.DateTimeFormatter

import aman.spothero.core
import aman.spothero.core.models.RatesPerDay
import aman.spothero.persistence.Days._
import cats.{Eq, Order}
import cats.data.NonEmptyList
import cats.implicits._
import io.circe.Decoder
import io.circe.generic.JsonCodec

@JsonCodec(decodeOnly = true)
case class Rates(rates: NonEmptyList[Rate])
object Rates {
  def convert(rates: Rates): RatesPerDay = {
    val empty: RatesPerDay = Map.empty

    val listOfDaysAndRates = for {
      (days, rates) <- rates.rates
        .groupBy(rates => rates.days.value)
        .toList
      day <- days.toList
    } yield (day, rates)

    listOfDaysAndRates.foldLeft(empty) {
      case (map, (day, rates)) =>
        map.updatedWith(day) { maybeRates =>
          val xs = maybeRates.map(_.toList).getOrElse(List.empty)
          Some(rates.map(Rate.convert) ++ xs)
        }
    }
  }
}

@JsonCodec(decodeOnly = true)
case class Rate(
    days: Days,
    times: RatePeriod,
    tz: ZoneId,
    price: BigDecimal
) {
  def isActiveOnDay(day: DayOfWeek): Boolean =
    days.value.contains_(day)
}
object Rate {
  def convert(rate: Rate): core.Rate =
    core.Rate(
      start = rate.times.start,
      end = rate.times.end,
      zoneId = rate.tz,
      price = rate.price
    )
}

case class Days(value: NonEmptyList[DayOfWeek]) extends AnyVal
object Days {
  implicit def decoderDays: Decoder[Days] =
    Decoder.decodeString
      .emap { s =>
        s.split(",")
          .toList
          .toNel
          .toRight(s"Unable to parse DayOfWeek from $s")
          .flatMap(
            _.traverse(
              s =>
                DayOfWeek
                  .values()
                  .find(_.toString.toLowerCase.startsWith(s.trim.toLowerCase))
                  .toRight(s"Unable to parse DayOfWeek from $s")
            )
          )
          .map(Days(_))
      }

  implicit val eqDayOfWeek: Eq[DayOfWeek] = Eq.fromUniversalEquals[DayOfWeek]
  implicit val orderDayOfWeek: Order[DayOfWeek] = Order.fromComparable[DayOfWeek]
}

case class RatePeriod private (
    start: LocalTime,
    end: LocalTime
)
object RatePeriod {
  def apply(start: LocalTime, end: LocalTime): Either[String, RatePeriod] =
    if (start.isBefore(end)) {
      new RatePeriod(start, end).asRight
    } else "Invalid period".asLeft

  implicit val decoderRatePeriod: Decoder[RatePeriod] =
    Decoder.decodeString
      .emap { s =>
        s.split("-") match {
          case Array(start, end) =>
            for {
              startTime <- toLocalTime(start, s"Unable to parse start time from $start")
              endTime <- toLocalTime(end, s"Unable to parse end time from $end")
              ratePeriod <- RatePeriod(startTime, endTime)
            } yield ratePeriod
          case _ => s"Unable to parse rate times from $s".asLeft
        }
      }

  val ratePeriodTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("kkmm")

  def toLocalTime(s: String, errorMsg: String): Either[String, LocalTime] =
    Either
      .catchNonFatal(LocalTime.parse(s, ratePeriodTimeFormatter))
      .leftMap(e => s"$errorMsg (${e.getMessage})")
}
