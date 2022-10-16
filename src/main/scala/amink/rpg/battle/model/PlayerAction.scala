package amink.rpg.battle.model

enum PlayerAction(val name: String, val typ: PlayerActionType):
  case Attack extends PlayerAction("attack", PlayerActionType.TargetsMonster)
  case Move extends PlayerAction("move", PlayerActionType.NeedsDirection)
  case Shove extends PlayerAction("shove", PlayerActionType.TargetsMonster)
  case Wait extends PlayerAction("wait", PlayerActionType.TargetsSelf)
