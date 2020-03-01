package aman.spothero.core

import java.time.ZonedDateTime

import aman.spothero.persistence.Rates
import cats.implicits._
import io.circe.parser._

class CalculatorSpec extends org.specs2.mutable.Specification {

  "Calculator" >> {
    "return 1750" >> {
      returns1750()
    }
    "return 2000" >> {
      returns2000()
    }
    "return unavailable for no rates for a valid period" >> {
      returnsUnavailableForNoRates()
    }
    "return unavailable for a invalid period greater than a day" >> {
      returnsUnavailableParkingPeriodOverMultipleDays()
    }
  }

  private[this] val sampleRatesJson =
    """
      |{
      |  "rates": [
      |    {
      |      "days": "mon,tues,thurs",
      |      "times": "0900-2100",
      |      "tz": "America/Chicago",
      |      "price": 1500
      |    },
      |    {
      |      "days": "fri,sat,sun",
      |      "times": "0900-2100",
      |      "tz": "America/Chicago",
      |      "price": 2000
      |    },
      |    {
      |      "days": "wed",
      |      "times": "0600-1800",
      |      "tz": "America/Chicago",
      |      "price": 1750
      |    },
      |    {
      |      "days": "mon,wed,sat",
      |      "times": "0100-0500",
      |      "tz": "America/Chicago",
      |      "price": 1000
      |    },
      |    {
      |      "days": "sun,tues",
      |      "times": "0100-0700",
      |      "tz": "America/Chicago",
      |      "price": 925
      |    }
      |  ]
      |}
      |""".stripMargin

  private[this] def rateForParkingPeriod(parkingPeriod: ParkingPeriod) =
    for {
      rawRates <- decode[Rates](sampleRatesJson).leftMap(_.getMessage)
      rates = Rates.convert(rawRates)
      cost <- Calculator.calculateParkingCost(rates, parkingPeriod)
    } yield cost

  private[this] def returns1750() =
    ParkingPeriod(
      ZonedDateTime.parse("2015-07-01T07:00:00-05:00"),
      ZonedDateTime.parse("2015-07-01T12:00:00-05:00")
    ).flatMap(rateForParkingPeriod) must beEqualTo(BigDecimal(1750).asRight)

  private[this] def returns2000() =
    ParkingPeriod(
      ZonedDateTime.parse("2015-07-04T15:00:00+00:00"),
      ZonedDateTime.parse("2015-07-04T20:00:00+00:00")
    ).flatMap(rateForParkingPeriod) must beEqualTo(BigDecimal(2000).asRight)

  private[this] def returnsUnavailableForNoRates() =
    ParkingPeriod(
      ZonedDateTime.parse("2015-07-04T07:00:00+05:00"),
      ZonedDateTime.parse("2015-07-04T20:00:00+05:00")
    ).flatMap(rateForParkingPeriod) must beEqualTo("Unavailable (no rates found)".asLeft)

  private[this] def returnsUnavailableParkingPeriodOverMultipleDays() =
    ParkingPeriod(
      ZonedDateTime.parse("2015-07-04T07:00:00+05:00"),
      ZonedDateTime.parse("2015-07-05T20:00:00+05:00")
    ).flatMap(rateForParkingPeriod) must beEqualTo(
      "Invalid parking period (2015-07-04T07:00+05:00 and 2015-07-05T20:00+05:00 cannot span multiple days)".asLeft
    )
}
