package aman.spothero.persistence

import cats.effect.{Resource, Sync}
import cats.implicits._
import io.circe.parser._

trait RatesRepository[F[_]] {
  def getRates: F[Rates]

  def writeRates(rates: Rates): F[Unit]
}

object RatesRepository {

  def readRatesFromDisk[F[_]](filepath: String)(implicit F: Sync[F]): F[Rates] = {
    val acquire = F.delay {
      scala.io.Source.fromInputStream(RatesRepository.getClass.getResourceAsStream(filepath))
    }
    Resource
      .fromAutoCloseable(acquire)
      .use(source => F.delay(source.mkString).map(s => decode[Rates](s)))
      .rethrow
  }

  def impl[F[_]: Sync]: RatesRepository[F] = new RatesRepository[F] {
    private val ratesFile = "/rates.json"
    private var ratesCache = Option.empty[Rates]

    override def getRates: F[Rates] = ratesCache match {
      case None        => readRatesFromDisk(ratesFile)
      case Some(rates) => rates.pure[F]
    }

    override def writeRates(rates: Rates): F[Unit] = Sync[F].delay {
      ratesCache = Some(rates)
    }
  }
}
