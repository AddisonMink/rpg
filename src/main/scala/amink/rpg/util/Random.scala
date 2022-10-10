package amink.rpg.util

import cats.Monad
import cats.data.State
import cats.implicits._
import cats.mtl.Stateful

object Random:

  /** Use Seed state to generate a random Int and a new Seed state.
    *
    * @param max
    *   highest number to generate.
    * @param F
    *   Monad with a Stateful instance.
    * @return
    *   An Int in the inclusive range [0,max].
    */
  def nextInt[F[_]: Monad](
      max: Int
  )(implicit F: Stateful[F, Seed]): F[Int] =
    for {
      result <- F.inspect(_.int(max))
      _ <- F.modify(_.next)
    } yield result

  /** Use Seed state to generate a random Int in a range and a new Seed state.
    *
    * @param min
    *   Low end of inclusive range.
    * @param max
    *   High end of inclusive range.
    * @param F
    *   Monand with a Stateful instance.
    * @return
    *   An Int in the inclusive range [min,max].
    */
  def nextInt[F[_]: Monad](
      min: Int,
      max: Int
  )(implicit F: Stateful[F, Seed]): F[Int] =
    for {
      result <- F.inspect(_.int(min, max))
      _ <- F.modify(_.next)
    } yield result

  def getSeed[F[_]: Monad](implicit F: Stateful[F, Seed]): F[Seed] =
    F.get
