package amink.rpg.util

import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

class SeedTests extends AnyFlatSpecLike with Matchers {
  import Seed.*

  behavior of "Cycle.next"

  it should "cycle through the given values" in {
    val cycle0 = Cycle(List(1, 2, 3))
    val cycle1 = cycle0.next
    val cycle2 = cycle1.next
    val cycle3 = cycle2.next

    cycle1 shouldBe (Cycle(List(2, 3, 1)))
    cycle2 shouldBe (Cycle(List(3, 1, 2)))
    cycle3 shouldBe cycle0
  }

  behavior of "int(max)"

  it should "return a number in [0,max]" in {
    Cycle(List(10)).int(10) shouldBe 10
    Cycle(List(11)).int(10) shouldBe 0
    Cycle(List(5)).int(10) shouldBe 5
  }

  behavior of "int(min,max)"

  it should "return a number in [min,max]" in {
    Cycle(List(1)).int(1, 2) shouldBe 2
    Cycle(List(3)).int(5, 10) shouldBe 8
    Cycle(List(0)).int(5, 10) shouldBe 5
    Cycle(List(5)).int(5, 10) shouldBe 10
  }

  behavior of "seedResultingIn"

  it should "return a seed value that will result in the given value" in {
    Seed.seedResultingIn(5, 10, 8) shouldBe 3
  }
}
