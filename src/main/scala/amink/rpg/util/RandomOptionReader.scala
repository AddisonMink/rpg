package amink.rpg.util

import cats.data.OptionT
import cats.data.ReaderT
import cats.data.State
import cats.implicits.*

import cats.mtl.Stateful
import cats.mtl.Ask
import cats.mtl.Tell
import cats.mtl.implicits.*

class RandomOptionReader[Config]:

  final case class Result[A](
      seed: Seed,
      value: Option[A]
  )

  private type RNG[A] = State[Seed, A]
  private type ReaderRNG[A] = ReaderT[RNG, Config, A]
  opaque type Stack[A] = OptionT[ReaderRNG, A]

  // Applicative
  def pure[A](a: A): Stack[A] = OptionT.pure(a)

  // Option
  def none[A]: Stack[A] = OptionT.none

  // Reader
  val ask: Stack[Config] = Ask.ask
  def query[A](f: Config => A): Stack[A] = ask.map(f)
  def queryOpt[A](f: Config => Option[A]): Stack[A] = query(f).flatMap(lift)

  // Random
  def nextInt(max: Int): Stack[Int] = Random.nextInt(max)
  def nextInt(min: Int, max: Int): Stack[Int] = Random.nextInt(min, max)
  val getSeed: Stack[Seed] = Random.getSeed

  // Extensions
  extension [A](m: Stack[A])
    def map[B](f: A => B): Stack[B] = m.map(f)
    def flatMap[B](f: A => Stack[B]): Stack[B] = m.flatMap(f)
    def filter(f: A => Boolean): Stack[A] = m.filter(f)
    def withFilter(f: A => Boolean): Stack[A] = filter(f)
    def filterNot(f: A => Boolean): Stack[A] = m.filterNot(f)
    def orElse(m1: Stack[A]): Stack[A] = m.orElse(m1)

    def run(seed: Seed, config: Config): Result[A] =
      val (newSeed, value) = m.value.run(config).run(seed).value
      Result(newSeed, value)

  extension [A](as: List[A])
    def trav[B](f: A => Stack[B]): Stack[List[B]] =
      as.traverse(f)

  extension [A](mas: List[Stack[A]]) def sequ: Stack[List[A]] = mas.sequence

  extension [A](aOpt: Option[A]) def lift: Stack[A] = OptionT.fromOption(aOpt)
