package amink.rpg.battle.control

import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

import amink.rpg.util.Seed
import amink.rpg.battle.model.*

class ActionExecutorTests extends AnyFlatSpecLike with Matchers:

  val goon = Species.Goon(
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
