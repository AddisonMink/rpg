package amink.rpg.battle.model

enum Action:
  case Attack(id: Id, targetId: Id, weapon: Weapon)
  case Move(id: Id, direction: Direction)
  case Shove(shoverId: Id, targetId: Id)
  case Wait(id: Id)
