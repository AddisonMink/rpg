package amink.rpg.battle.model

enum State(val creatureMap: CreatureMap):

  case Selecting(
      override val creatureMap: CreatureMap,
      id: Id,
      actions: List[PlayerAction],
      index: Int
  ) extends State(creatureMap)

  case TargetingMonster(
      override val creatureMap: CreatureMap,
      id: Id,
      action: PlayerAction,
      targets: List[Id],
      index: Int
  ) extends State(creatureMap)

  case MonsterActing(
      override val creatureMap: CreatureMap,
      id: Id
  ) extends State(creatureMap)

  case ExecutingAction(
      override val creatureMap: CreatureMap,
      actions: Action
  ) extends State(creatureMap)

  case Logging(
      override val creatureMap: CreatureMap,
      logs: List[Log]
  ) extends State(creatureMap)

  case Won(
      override val creatureMap: CreatureMap
  ) extends State(creatureMap)

  case Lost(
      override val creatureMap: CreatureMap
  ) extends State(creatureMap)
