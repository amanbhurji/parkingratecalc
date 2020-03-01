package aman.spothero.api

import aman.spothero.api.models.Rates
import aman.spothero.persistence.RatesRepository
import cats.Applicative

trait RatesManager[F[_]] {
  def updateRates(rates: Rates): F[Unit]
}
object RatesManager {
  implicit def apply[F[_]](implicit ev: ParkingCalculator[F]): ParkingCalculator[F] = ev

  def impl[F[_]: Applicative](R: RatesRepository[F]): RatesManager[F] =
    new RatesManager[F] {
      override def updateRates(rates: Rates): F[Unit] =
        R.writeRates(rates)
    }
}
