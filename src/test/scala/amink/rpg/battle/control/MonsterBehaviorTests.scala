package amink.rpg.battle.control

import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

import amink.rpg.battle.model.*
import amink.rpg.util.Seed

class MonsterBehaviorTests extends AnyFlatSpecLike with Matchers:
  import Action.*
  import Species.*

  private val seed = Seed.Cycle(Nil)
  private val fighter = Fighter(Weapon("", Range.Close, 1, 0, 1))

  behavior of "goblin"

  it should "attack with its bow if its in the back row" in {
    val goblin = Creature.make(0, "1", Goblin, Row.Back)
    val player = Creature.make(1, "1", fighter, Row.Front)
    val cMap = Map(0 -> goblin, 1 -> player)

    val (_, action) = MonsterBehavior.selectAction(cMap, 0, seed)

    action shouldBe Attack(0, 1, goblinBow)
  }

  it should "attack with its weapon if its in the front row and it can hit something" in {
    val goblin = Creature.make(0, "1", Goblin, Row.Front)
    val player = Creature.make(1, "1", fighter, Row.Front)
    val cMap = Map(0 -> goblin, 1 -> player)

    val (_, action) = MonsterBehavior.selectAction(cMap, 0, seed)

    action shouldBe Attack(0, 1, Goblin.weapon)
  }

  it should "move back if its in the front row and it can't hit anything" in {
    val goblin = Creature.make(0, "1", Goblin, Row.Front)
    val player = Creature.make(1, "1", fighter, Row.Back)
    val cMap = Map(0 -> goblin, 1 -> player)

    val (_, action) = MonsterBehavior.selectAction(cMap, 0, seed)

    action shouldBe Move(0, Direction.Back)
  }
