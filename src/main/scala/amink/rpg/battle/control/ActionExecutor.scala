package amink.rpg.battle.control

import monocle.syntax.all.*

import amink.rpg.util.*
import amink.rpg.battle.model.*

object ActionExecutor {
  import Action.*
  import Log.*
  private val monadStack = RandomReaderWriterStateStack[Unit, Log, CreatureMap]
  import monadStack.*

  final case class ActionResult(
      creatureMap: CreatureMap,
      seed: Seed,
      logs: List[Log]
  )

  def execute(
      creatureMap: CreatureMap,
      action: Action,
      seed: Seed
  ): ActionResult =
    val result = executor(action).run(seed, (), creatureMap)
    ActionResult(result.state, result.seed, result.logs)

  private type Executor[A] = Stack[A]

  private def executor(action: Action): Executor[Unit] = action match
    case Attack(id, targetId, weapon) => attack(id, targetId, weapon)
    case Move(id, direction)          => move(id, direction)
    case Shove(shoverId, targetId)    => ???
    case Wait(id)                     => ???

  private def attack(id: Id, targetId: Id, weapon: Weapon): Executor[Unit] =
    for {
      attacker <- inspect(_(id))
      _ <- tell(AttackLog(attacker, weapon))

      actionCost = Math
        .round(attacker.species.actionCost * weapon.actionCostMultiplier)
        .toInt

      _ <- modifyCreature(id)(nextActionAt = _ + actionCost)

      attackStrength = attacker.species.strength
        * weapon.strengthMultiplier
        + weapon.damageBonus

      numDice = Math.round(attackStrength).toInt
      amount <- rollDice(numDice, 2)
      _ <- damage(targetId, amount)
    } yield ()

  private def move(id: Id, direction: Direction): Executor[Unit] = for {
    creature <- inspect(_(id))
    _ <- tell(MoveLog(creature, direction))

    newRow = (creature.row, direction) match
      case (Row.Front, Direction.Forward) => Row.Front
      case (Row.Front, Direction.Back)    => Row.Back
      case (Row.Back, Direction.Forward)  => Row.Front
      case (Row.Back, Direction.Back)     => Row.Back

    _ <- modifyCreature(id)(
      nextActionAt = _ + creature.species.actionCost,
      row = _ => newRow
    )

    _ <- (creature.row, direction) match
      case (Row.Front, Direction.Forward) =>
        rowReset(creature.species.team.opposite)
      case _ =>
        pure(())
  } yield ()

  private def damage(id: Id, amount: Int): Executor[Unit] = for {
    creature <- inspect(_(id))
    _ <- tell(DamageLog(creature, amount))
    _ <- modifyCreature(id)(hp = _ - amount)
    _ <- if creature.hp <= amount then die(id) else pure(())
  } yield ()

  private def die(id: Id): Executor[Unit] = for {
    creature <- inspect(_(id))
    _ <- tell(DeathLog(creature))
    _ <- modifyCreature(id)(state = _ => CreatureState.Dead)
  } yield ()

  private def rowReset(team: Team): Executor[Unit] = for {
    creatures <- inspect(_.team(team))
    _ = println(creatures)
    _ <-
      if creatures.exists(_.row == Row.Front) then pure(Nil)
      else
        creatures
          .map(_.id)
          .trav(modifyCreature(_)(row = _ => Row.Front))
  } yield ()

  private def rollDice(num: Int, size: Int): Executor[Int] = for {
    dice <- List.fill(num)(nextInt(1, size)).sequ
  } yield dice.sum

  private def modifyCreature(id: Id)(
      hp: Int => Int = identity,
      row: Row => Row = identity,
      nextActionAt: Int => Int = identity,
      state: CreatureState => CreatureState = identity
  ): Executor[Unit] =
    def f(c: Creature) = c.copy(
      hp = hp(c.hp),
      row = row(c.row),
      nextActionAt = nextActionAt(c.nextActionAt),
      state = state(c.state)
    )

    modify(_.focus(_.index(id)).modify(f))
}
