package amink.rpg.battle.model

import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

import amink.rpg.util.Seed

class StateTests extends AnyFlatSpecLike with Matchers:
  import State.*

  private val seed = Seed.Cycle(Nil)
  private val fighter = Species.Fighter(Species.fighterSword)

  behavior of "makeSelectingAction"

  it should "include the attack action if there are monsters in weapon range" in {
    import PlayerAction.*

    val player = Creature.make(0, "1", fighter, Row.Front)
    val monster = Creature.make(1, "1", Species.Goblin, Row.Front)
    val cMap = Map(0 -> player, 1 -> monster)

    val actions = List(Attack, Move, Shove, Wait)
    val expected = SelectingAction(seed, cMap, Map(), 0, actions, 0)

    val actual = makeSelectingAction(seed, cMap, Map(), 0)

    actual.actions shouldBe actions
    actual shouldBe expected
  }

  it should "not include the attack option if there are no monsters in range" in {
    import PlayerAction.*

    val player = Creature.make(0, "1", fighter, Row.Front)
    val monster = Creature.make(1, "1", Species.Goblin, Row.Back)
    val cMap = Map(0 -> player, 1 -> monster)

    val actions = List(Move, Wait)
    val expected = SelectingAction(seed, cMap, Map(), 0, actions, 0)

    val actual = makeSelectingAction(seed, cMap, Map(), 0)

    actual.actions shouldBe actions
    actual shouldBe expected
  }

  it should "include the shove action if there are monsters in shove range" in {
    import PlayerAction.*

    val player = Creature.make(0, "1", fighter, Row.Front)
    val monster = Creature.make(1, "1", Species.Goblin, Row.Front)
    val cMap = Map(0 -> player, 1 -> monster)

    val actions = List(Attack, Move, Shove, Wait)
    val expected = SelectingAction(seed, cMap, Map(), 0, actions, 0)

    val actual = makeSelectingAction(seed, cMap, Map(), 0)

    actual.actions shouldBe actions
    actual shouldBe expected
  }

  it should "not include the shove action if there are no monsters in range" in {
    import PlayerAction.*

    val player = Creature.make(0, "1", fighter, Row.Front)
    val monster = Creature.make(1, "1", Species.Goblin, Row.Back)
    val cMap = Map(0 -> player, 1 -> monster)

    val actions = List(Move, Wait)
    val expected = SelectingAction(seed, cMap, Map(), 0, actions, 0)

    val actual = makeSelectingAction(seed, cMap, Map(), 0)

    actual.actions shouldBe actions
    actual shouldBe expected
  }

  behavior of "makeSelectingDirection"

  it should "only include valid directions" in {
    import PlayerAction.*

    val player = Creature.make(0, "1", fighter, Row.Front)
    val monster = Creature.make(1, "1", Species.Goblin, Row.Front)
    val cMap = Map(0 -> player, 1 -> monster)

    val dirs = List(Direction.Back)
    val expected = SelectingDirection(seed, cMap, Map(), 0, Move, dirs, 0)

    val actual = makeSelectingDirection(seed, cMap, Map(), 0, Move)

    actual.get.directions shouldBe dirs
    actual.get shouldBe expected
  }

  behavior of "makeSelectingMonster"

  it should "only include monsters in the given range" in {
    import PlayerAction.*

    val player = Creature.make(0, "1", fighter, Row.Front)
    val monster1 = Creature.make(1, "1", Species.Goblin, Row.Front)
    val monster2 = Creature.make(2, "2", Species.Goblin, Row.Back)
    val cMap = Map(0 -> player, 1 -> monster1, 2 -> monster2)

    val expected = SelectingMonster(seed, cMap, Map(), 0, Attack, List(1), 0)

    val actual = makeSelectingMonster(seed, cMap, Map(), 0, Attack)

    actual.get.monsters shouldBe List(1)
    actual.get shouldBe expected
  }

  it should "return None if there are no monsters in range" in {
    import PlayerAction.*

    val player = Creature.make(0, "1", fighter, Row.Front)
    val monster1 = Creature.make(1, "1", Species.Goblin, Row.Back)
    val monster2 = Creature.make(2, "2", Species.Goblin, Row.Back)
    val cMap = Map(0 -> player, 1 -> monster1, 2 -> monster2)

    val expected = SelectingMonster(seed, cMap, Map(), 0, Attack, List(1), 0)

    val actual = makeSelectingMonster(seed, cMap, Map(), 0, Attack)

    actual shouldBe None
  }
