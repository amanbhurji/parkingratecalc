package aman.spothero.api

import aman.spothero.core.Calculator
import aman.spothero.persistence.{Rates, RatesRepository}
import cats.Applicative
import cats.implicits._

trait ParkingCalculator[F[_]] {
  def price(parkingPeriod: ParkingPeriod): F[Either[String, Cost]]
}

object ParkingCalculator {
  implicit def apply[F[_]](implicit ev: ParkingCalculator[F]): ParkingCalculator[F] = ev

  def impl[F[_]: Applicative](R: RatesRepository[F]): ParkingCalculator[F] =
    new ParkingCalculator[F] {
      override def price(parkingPeriod: ParkingPeriod): F[Either[String, Cost]] =
        R.getRates.map { rates =>
          for {
            convertedParkingPeriod <- ParkingPeriod.convert(parkingPeriod)
            convertedRates = Rates.convert(rates)
            cost <- Calculator.calculateParkingCost(convertedRates, convertedParkingPeriod)
          } yield Cost(cost)
        }
    }
}
