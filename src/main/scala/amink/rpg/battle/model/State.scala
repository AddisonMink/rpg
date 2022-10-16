package amink.rpg.battle.model

import amink.canvasui.Sprite

import amink.rpg.util.*

type SpriteName = String
type SpriteUrl = String

enum State(
    val seed: Seed,
    val creatureMap: CreatureMap,
    val sprites: Map[SpriteName, Sprite]
):

  case LoadingSprites(
      override val seed: Seed,
      override val creatureMap: CreatureMap,
      override val sprites: Map[SpriteName, Sprite],
      spriteSources: Map[SpriteName, SpriteUrl]
  ) extends State(seed, creatureMap, sprites)

  case SelectingAction(
      override val seed: Seed,
      override val creatureMap: CreatureMap,
      override val sprites: Map[SpriteName, Sprite],
      id: Id,
      actions: List[PlayerAction],
      index: Int
  ) extends State(seed, creatureMap, sprites)

  case SelectingDirection(
      override val seed: Seed,
      override val creatureMap: CreatureMap,
      override val sprites: Map[SpriteName, Sprite],
      id: Id,
      action: PlayerAction,
      directions: List[Direction],
      index: Int
  ) extends State(seed, creatureMap, sprites)

  case SelectingMonster(
      override val seed: Seed,
      override val creatureMap: CreatureMap,
      override val sprites: Map[SpriteName, Sprite],
      id: Id,
      action: PlayerAction,
      monsters: List[Id],
      index: Int
  ) extends State(seed, creatureMap, sprites)

  case MonsterActing(
      override val seed: Seed,
      override val creatureMap: CreatureMap,
      override val sprites: Map[SpriteName, Sprite],
      id: Id
  ) extends State(seed, creatureMap, sprites)

  case ExecutingAction(
      override val seed: Seed,
      override val creatureMap: CreatureMap,
      override val sprites: Map[SpriteName, Sprite],
      action: Action
  ) extends State(seed, creatureMap, sprites)

  case Logging(
      override val seed: Seed,
      override val creatureMap: CreatureMap,
      override val sprites: Map[SpriteName, Sprite],
      logs: List[Log]
  ) extends State(seed, creatureMap, sprites)

  case Won(
      override val seed: Seed,
      override val creatureMap: CreatureMap,
      override val sprites: Map[SpriteName, Sprite]
  ) extends State(seed, creatureMap, sprites)

  case Lost(
      override val seed: Seed,
      override val creatureMap: CreatureMap,
      override val sprites: Map[SpriteName, Sprite]
  ) extends State(seed, creatureMap, sprites)

  def selectNext: State = this match
    case s: SelectingAction =>
      s.copy(index = nextIndex(s.actions, s.index))
    case s: SelectingDirection =>
      s.copy(index = nextIndex(s.directions, s.index))
    case s: SelectingMonster =>
      s.copy(index = nextIndex(s.monsters, s.index))
    case state =>
      state

  def selectPrev: State = this match
    case s: SelectingAction =>
      s.copy(index = prevIndex(s.actions, s.index))
    case s: SelectingDirection =>
      s.copy(index = prevIndex(s.directions, s.index))
    case s: SelectingMonster =>
      s.copy(index = prevIndex(s.monsters, s.index))
    case state =>
      state

  private def nextIndex[A](as: List[A], index: Int): Int =
    if index == as.length - 1 then 0 else index + 1

  private def prevIndex[A](as: List[A], index: Int): Int =
    if index == 0 then as.length - 1 else index - 1

object State:

  def makeSelectingAction(
      seed: Seed,
      creatureMap: CreatureMap,
      sprites: Map[String, Sprite],
      id: Int
  ): SelectingAction =
    val attack = creatureMap.validWeaponTargets(id).nonEmpty
    val move = creatureMap.validDirections(id).nonEmpty
    val shove = creatureMap.validTargets(id, Range.Close).nonEmpty

    val actions = List(
      PlayerAction.Attack -> attack,
      PlayerAction.Move -> move,
      PlayerAction.Shove -> shove,
      PlayerAction.Wait -> true
    ).filter(_._2).map(_._1)

    SelectingAction(seed, creatureMap, sprites, id, actions, 0)

  def makeSelectingDirection(
      seed: Seed,
      creatureMap: CreatureMap,
      sprites: Map[String, Sprite],
      id: Id,
      action: PlayerAction
  ): Option[SelectingDirection] = for {
    dirs <- Some(creatureMap.validDirections(id))
    if action == PlayerAction.Move
    if dirs.nonEmpty
  } yield SelectingDirection(seed, creatureMap, sprites, id, action, dirs, 0)

  def makeSelectingMonster(
      seed: Seed,
      creatureMap: CreatureMap,
      sprites: Map[String, Sprite],
      id: Id,
      action: PlayerAction
  ): Option[SelectingMonster] = for {
    range <- action match
      case PlayerAction.Attack => Some(creatureMap(id).species.weapon.range)
      case PlayerAction.Shove  => Some(Range.Close)
      case _                   => None

    monsters = creatureMap.validTargets(id, range)
    if monsters.nonEmpty
  } yield SelectingMonster(seed, creatureMap, sprites, id, action, monsters, 0)
