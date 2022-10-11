package amink.rpg.battle.model

import amink.rpg.util.*

enum State(val seed: Seed, val creatureMap: CreatureMap):

  case SelectingAction(
      override val seed: Seed,
      override val creatureMap: CreatureMap,
      id: Id,
      actions: List[PlayerAction],
      index: Int
  ) extends State(seed, creatureMap)

  case SelectingDirection(
      override val seed: Seed,
      override val creatureMap: CreatureMap,
      id: Id,
      action: PlayerAction,
      directions: List[Direction],
      index: Int
  ) extends State(seed, creatureMap)

  case SelectingMonster(
      override val seed: Seed,
      override val creatureMap: CreatureMap,
      id: Id,
      action: PlayerAction,
      monsters: List[Id],
      index: Int
  ) extends State(seed, creatureMap)

  case MonsterActing(
      override val seed: Seed,
      override val creatureMap: CreatureMap,
      id: Id
  ) extends State(seed, creatureMap)

  case ExecutingAction(
      override val seed: Seed,
      override val creatureMap: CreatureMap,
      action: Action
  ) extends State(seed, creatureMap)

  case Logging(
      override val seed: Seed,
      override val creatureMap: CreatureMap,
      logs: List[Log]
  ) extends State(seed, creatureMap)

  case Won(
      override val seed: Seed,
      override val creatureMap: CreatureMap
  ) extends State(seed, creatureMap)

  case Lost(
      override val seed: Seed,
      override val creatureMap: CreatureMap
  ) extends State(seed, creatureMap)

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
