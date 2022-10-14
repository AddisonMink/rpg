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
