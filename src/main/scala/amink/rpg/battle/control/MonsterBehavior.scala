package amink.rpg.battle.control

import amink.rpg.battle.model.*
import amink.rpg.util.*

object MonsterBehavior:
  import Action.*
  import Species.*

  private case class Config(id: Id, cMap: CreatureMap)

  private val stack = RandomOptionReader[Config]
  import stack.*
  private type Behavior[A] = Stack[A]

  def selectAction(
      creatureMap: CreatureMap,
      id: Id,
      seed: Seed
  ): (Seed, Action) =
    val result = behavior.run(seed, Config(id, creatureMap))
    val action = result.value.getOrElse(Wait(id))
    (result.seed, action)

  private def behavior: Behavior[Action] = for {
    creature <- actor
    action <- creature.species match
      case Goblin => goblinBehavior
      case Ogre   => ???
      case _      => none
  } yield action

  private def goblinBehavior: Behavior[Action] =
    goblinBow
      .orElse(weaponAttack)
      .orElse(move(Direction.Back))

  private def goblinBow: Behavior[Action] = for {
    me <- actor
    if me.row == Row.Back
    players <- query(_.cMap.players)
    player <- players
      .sortBy(_.hp)
      .headOption
      .lift
  } yield Attack(me.id, player.id, Species.goblinBow)

  private def weaponAttack: Behavior[Action] = for {
    me <- actor
    range = me.species.weapon.range.canTarget(me.row)
    players <- query(_.cMap.players)
    player <- players
      .filter(p => range.contains(p.row))
      .sortBy(_.hp)
      .headOption
      .lift
  } yield Attack(me.id, player.id, me.species.weapon)

  private def move(direction: Direction): Behavior[Action] = for {
    id <- query(_.id)
  } yield Move(id, direction)

  private def actor: Behavior[Creature] = for {
    id <- query(_.id)
    creature <- query(_.cMap(id))
  } yield creature
