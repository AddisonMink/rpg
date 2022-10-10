package amink.rpg.util

/** Pure, testable RNG.
  *   - Rand is a true, seedable RNG that wraps scala.util.Random.
  *   - Cycle will cycle through the given list of values.
  */
enum Seed:
  case Rand(seed: Int)
  case Cycle(values: List[Int])

  def next: Seed = this match
    case Rand(seed)     => Rand(scala.util.Random(seed).nextInt())
    case Cycle(v :: vs) => Cycle(vs :+ v)
    case Cycle(Nil)     => Cycle(Nil)

  def int(max: Int): Int = this match
    case Rand(seed)    => seed % (max + 1)
    case Cycle(v :: _) => v % (max + 1)
    case Cycle(Nil)    => 0

  def int(min: Int, max: Int): Int = this match
    case Rand(seed)    => int(max - min) + min
    case Cycle(v :: _) => int(max - min) + min
    case Cycle(Nil)    => 0

object Seed:
  def seedResultingIn(min: Int, max: Int, result: Int): Int =
    result - min
