package aman.spothero.core

import aman.spothero.core.models.RatesPerDay

object Calculator {

  def calculateParkingCost(
      rates: RatesPerDay,
      parkingPeriod: ParkingPeriod
  ): Either[String, BigDecimal] = {
    val ratesForDay = rates.get(parkingPeriod.start.getDayOfWeek)
    val startDateTime = parkingPeriod.start.toLocalDateTime

    ratesForDay
      .flatMap(_.find { rate =>
        val offset = rate.zoneId.getRules.getOffset(startDateTime)
        val rateOffsetStart = rate.start.atOffset(offset)
        val rateOffsetEnd = rate.end.atOffset(offset)

        val rateContainsParkingPeriod = !rateOffsetStart
          .isAfter(parkingPeriod.start.toOffsetDateTime.toOffsetTime) &&
          !parkingPeriod.end.toOffsetDateTime.toOffsetTime.isAfter(rateOffsetEnd)
        rateContainsParkingPeriod
      })
      .map(_.price)
      .toRight("Unavailable (no rates found)")
  }

}
