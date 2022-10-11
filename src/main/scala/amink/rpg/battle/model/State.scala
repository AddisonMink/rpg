package amink.rpg.battle.model

import amink.rpg.util.*

enum State(val seed: Seed, val creatureMap: CreatureMap):

  case Selecting(
      override val seed: Seed,
      override val creatureMap: CreatureMap,
      id: Id,
      actions: List[PlayerAction],
      index: Int
  ) extends State(seed, creatureMap)

  case TargetingMonster(
      override val seed: Seed,
      override val creatureMap: CreatureMap,
      id: Id,
      action: PlayerAction,
      targets: List[Id],
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
      actions: Action
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
