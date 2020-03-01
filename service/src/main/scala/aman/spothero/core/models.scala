package aman.spothero.core

import java.time.{DayOfWeek, LocalTime, ZoneId, ZonedDateTime}

import cats.data.NonEmptyList
import cats.implicits._

object models {
  type RatesPerDay = Map[DayOfWeek, NonEmptyList[Rate]]
}
case class Rate(
    start: LocalTime,
    end: LocalTime,
    zoneId: ZoneId,
    price: BigDecimal
)
case class ParkingPeriod private (start: ZonedDateTime, end: ZonedDateTime)
object ParkingPeriod {
  def apply(start: ZonedDateTime, end: ZonedDateTime): Either[String, ParkingPeriod] =
    (start, end) match {
      case _ if !start.isBefore(end) =>
        s"Invalid parking period ($start was not before $end)".asLeft

      case _ if start.getDayOfYear =!= end.getDayOfYear =>
        s"Invalid parking period ($start and $end cannot span multiple days)".asLeft

      case _ =>
        new ParkingPeriod(start, end).asRight
    }
}
