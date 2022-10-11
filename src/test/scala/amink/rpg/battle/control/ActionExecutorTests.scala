package amink.rpg.battle.control

import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

import amink.rpg.util.Seed
import amink.rpg.battle.model.*

class ActionExecutorTests extends AnyFlatSpecLike with Matchers:

  val goon: Species.Goon = Species.Goon(
    name = "goon",
    team = Team.Players,
    maxHp = 100,
    size = Size.Medium,
    strength = 10,
    actionCost = 100,
    weapon = Weapon(
      name = "club",
      range = Range.Close,
      strengthMultiplier = 1.5,
      damageBonus = 1,
      actionCostMultiplier = 1.5
    )
  )

  val monsterGoon = goon.copy(team = Team.Monsters)

  behavior of "attack"

  it should
    "log the attack" +
    ", adjust nextActionAt" +
    ", log damage" +
    ", damage target" in {

      // Set up initial state.
      val attacker = Creature.make(1, "1", goon, Row.Front)
      val target = attacker.copy(id = 2, nameSuffix = "2")
      val creatureMap = Map(1 -> attacker, 2 -> target)
      val action = Action.Attack(1, 2, goon.weapon)

      // Expected values.
      val attackLog = Log.AttackLog(attacker, goon.weapon)
      val apCost = 150
      val damageDice = 16
      val seed = Seed.Cycle(List(1))
      val damage = 2 * damageDice
      val damageLog = Log.DamageLog(target, damage)

      // Test
      val result = ActionExecutor.execute(creatureMap, action, seed)
      result.creatureMap(1).nextActionAt shouldBe attacker.nextActionAt + apCost
      result.creatureMap(2).hp shouldBe target.hp - damage
      result.creatureMap(2).state shouldBe CreatureState.Alive
      result.logs shouldBe List(attackLog, damageLog)
    }

  it should "kill the target if its hp drops to 0" in {

    // Set up initial state.
    val attacker = Creature.make(1, "1", goon, Row.Front)
    val target = attacker.copy(id = 2, nameSuffix = "2", hp = 1)
    val creatureMap = Map(1 -> attacker, 2 -> target)
    val action = Action.Attack(1, 2, goon.weapon)

    // Expected values.
    val attackLog = Log.AttackLog(attacker, goon.weapon)
    val apCost = 150
    val damageDice = 16
    val seed = Seed.Cycle(List(1))
    val damage = 2 * damageDice
    val damageLog = Log.DamageLog(target, damage)
    val deathLog = Log.DeathLog(target.copy(hp = target.hp - damage))

    // Test
    val result = ActionExecutor.execute(creatureMap, action, seed)
    result.creatureMap(1).nextActionAt shouldBe attacker.nextActionAt + apCost
    result.creatureMap(2).hp shouldBe target.hp - damage
    result.creatureMap(2).state shouldBe CreatureState.Dead
    result.logs shouldBe List(attackLog, damageLog, deathLog)
  }

  behavior of "move"

  it should "move from front to back" in {
    // Set up initial state.
    val creature = Creature.make(1, "1", goon, Row.Front)
    val other = creature.copy(id = 2, "2", row = Row.Back)
    val creatureMap = Map(1 -> creature, 2 -> other)
    val action = Action.Move(1, Direction.Back)

    // Expected values.
    val moveLog = Log.MoveLog(creature, Direction.Back)
    val apCost = 100

    // Test
    val result = ActionExecutor.execute(creatureMap, action, Seed.Cycle(Nil))
    result.creatureMap(1).nextActionAt shouldBe creature.nextActionAt + apCost
    result.creatureMap(1).row shouldBe Row.Back
    result.creatureMap(2).row shouldBe other.row
    result.logs shouldBe List(moveLog)
  }

  it should "move from back to front" in {
    // Set up initial state.
    val creature = Creature.make(1, "1", goon, Row.Back)
    val other = creature.copy(id = 2, "2", row = Row.Back)
    val creatureMap = Map(1 -> creature, 2 -> other)
    val action = Action.Move(1, Direction.Forward)

    // Expected values.
    val moveLog = Log.MoveLog(creature, Direction.Forward)
    val apCost = 100

    // Test
    val result = ActionExecutor.execute(creatureMap, action, Seed.Cycle(Nil))
    result.creatureMap(1).nextActionAt shouldBe creature.nextActionAt + apCost
    result.creatureMap(1).row shouldBe Row.Front
    result.creatureMap(2).row shouldBe other.row
    result.logs shouldBe List(moveLog)
  }

  it should "trigger a row reset if moving forward from font row" in {
    // Set up initial state.
    val creature = Creature.make(1, "1", goon, Row.Front)
    val other =
      creature.copy(id = 2, "2", row = Row.Back, species = monsterGoon)
    val creatureMap = Map(1 -> creature, 2 -> other)
    val action = Action.Move(1, Direction.Forward)

    // Expected values.
    val moveLog = Log.MoveLog(creature, Direction.Forward)
    val apCost = 100

    // Test
    val result = ActionExecutor.execute(creatureMap, action, Seed.Cycle(Nil))
    result.creatureMap(1).nextActionAt shouldBe creature.nextActionAt + apCost
    result.creatureMap(1).row shouldBe Row.Front
    result.creatureMap(2).row shouldBe Row.Front
    result.logs shouldBe List(moveLog)
  }

  behavior of "shove"

  it should "move the target back and trigger a row reset if the shover wins the strength roll" in {
    // Set up initial state.
    val creature = Creature.make(1, "1", goon, Row.Front)
    val other =
      creature.copy(id = 2, "2", row = Row.Front, species = monsterGoon)
    val other3 = other.copy(id = 3, "2", row = Row.Back)
    val creatureMap = Map(1 -> creature, 2 -> other, 3 -> other3)
    val action = Action.Shove(1, 2)

    // Expected values.
    val shoveLog = Log.ShoveLog(creature, other, true)
    val apCost = 100

    val seed =
      Seed.Cycle(List.fill(goon.strength)(1) ++ List.fill(goon.strength)(0))

    // Test
    val result = ActionExecutor.execute(creatureMap, action, seed)
    result.creatureMap(1).nextActionAt shouldBe creature.nextActionAt + apCost
    result.creatureMap(2).row shouldBe Row.Front
    result.creatureMap(3).row shouldBe Row.Front
    result.logs shouldBe List(shoveLog)
  }

  it should "not move the target if the shover loses the strength roll" in {
    // Set up initial state.
    val creature = Creature.make(1, "1", goon, Row.Front)
    val other =
      creature.copy(id = 2, "2", row = Row.Front, species = monsterGoon)
    val other3 = other.copy(id = 3, "2", row = Row.Back)
    val creatureMap = Map(1 -> creature, 2 -> other, 3 -> other3)
    val action = Action.Shove(1, 2)

    // Expected values.
    val shoveLog = Log.ShoveLog(creature, other, false)
    val apCost = 100

    val seed =
      Seed.Cycle(List.fill(goon.strength)(0) ++ List.fill(goon.strength)(1))

    // Test
    val result = ActionExecutor.execute(creatureMap, action, seed)
    result.creatureMap(1).nextActionAt shouldBe creature.nextActionAt + apCost
    result.creatureMap(2).row shouldBe Row.Front
    result.creatureMap(3).row shouldBe Row.Back
    result.logs shouldBe List(shoveLog)
  }

  behavior of "wait"

  it should "log the wait and adjust nextActionAt" in {
    // Set up initial state.
    val creature = Creature.make(1, "1", goon, Row.Front)
    val creatureMap = Map(1 -> creature)
    val action = Action.Wait(1)

    // Expected values.
    val waitLog = Log.WaitLog(creature)
    val apCost = 25

    // Test
    val result = ActionExecutor.execute(creatureMap, action, Seed.Cycle(Nil))
    result.creatureMap(1).nextActionAt shouldBe creature.nextActionAt + apCost
    result.logs shouldBe List(waitLog)
  }
