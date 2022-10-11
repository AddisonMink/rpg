package amink.rpg.battle.model

enum PlayerAction(val typ: PlayerActionType):
  case Attack extends PlayerAction(PlayerActionType.TargetsMonster)
  case Move extends PlayerAction(PlayerActionType.NeedsDirection)
  case Shove extends PlayerAction(PlayerActionType.TargetsMonster)
  case Wait extends PlayerAction(PlayerActionType.TargetsSelf)

  def toSelfTargetingAction(id: Id): Action = this match
    case _ => Action.Wait(id)

  def toDirectionalAction(id: Id, dir: Direction) = this match
    case Move => Action.Move(id, dir)
    case _    => Action.Wait(id)

  def toMonsterTargetingAction(
      id: Id,
      targetId: Id,
      weapon: Option[Weapon] = None
  ): Action = (this, weapon) match
    case (Attack, Some(w)) => Action.Attack(id, targetId, w)
    case (Shove, None)     => Action.Shove(id, targetId)
    case _                 => Action.Wait(id)
