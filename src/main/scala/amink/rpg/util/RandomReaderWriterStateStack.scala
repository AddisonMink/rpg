package amink.rpg.util

import cats.data.RWST
import cats.implicits.*

import cats.mtl.Stateful
import cats.mtl.Ask
import cats.mtl.Tell
import cats.mtl.implicits.*

/** Utility class that facilitates use of the Random, Reader, Writer, State
  * monad stack.
  *
  * It creates a dependent type with the given RWS parameters so that the
  * compiler will never need help with type inference.
  *
  * It exposes wrappers for all the relevant functions for the monad stack, so
  * its not necessary to import anything other than this class.
  */
final class RandomReaderWriterStateStack[Config, Log, State]:

  final case class Result[A](
      seed: Seed,
      logs: List[Log],
      state: State,
      value: A
  )

  private type RNG[A] = cats.data.State[Seed, A]

  opaque type Stack[A] = RWST[RNG, Config, Vector[Log], State, A]

  // Applicative
  def pure[A](a: A): Stack[A] = RWST.pure(a)

  // Reader
  val ask: Stack[Config] = Ask.ask
  def query[A](f: Config => A): Stack[A] = ask.map(f)

  // Writer
  def tell[L](l: Log): Stack[Unit] = Tell.tell(Vector(l))

  // State
  val get: Stack[State] = Stateful.get
  def inspect[A](f: State => A): Stack[A] = Stateful.inspect(f)
  def set(s: State): Stack[Unit] = Stateful.set(s)
  def modify(f: State => State): Stack[Unit] = Stateful.modify(f)

  // Random
  def nextInt(max: Int): Stack[Int] = Random.nextInt(max)
  def nextInt(min: Int, max: Int): Stack[Int] = Random.nextInt(min, max)
  val getSeed: Stack[Seed] = Random.getSeed

  // Extensions
  extension [A](m: Stack[A])
    def map[B](f: A => B): Stack[B] = m.map(f)
    def flatMap[B](f: A => Stack[B]): Stack[B] = m.flatMap(f)

    def run(seed: Seed, config: Config, state: State): Result[A] =
      val s = m.run(config, state)
      val (newSeed, (logs, newState, value)) = s.run(seed).value
      Result(newSeed, logs.toList, newState, value)

  extension [A](as: List[A])
    def trav[B](f: A => Stack[B]): Stack[List[B]] =
      as.traverse(f)

  extension [A](mas: List[Stack[A]]) def sequ: Stack[List[A]] = mas.sequence
