package amink.rpg.battle.model

enum Log:
  case AttackLog(attacker: Creature, weapon: Weapon)
  case DamageLog(creature: Creature, amount: Int)
  case DeathLog(creature: Creature)
  case WaitLog(creature: Creature)
  case ShoveLog(shover: Creature, target: Creature, success: Boolean)

  def message: String = this match
    case AttackLog(attacker, weapon) =>
      s"${attacker.name} attacks with a ${weapon.name}"
    case DamageLog(creature, amount) =>
      s"${creature.name} takes $amount damage"
    case DeathLog(creature) =>
      s"${creature.name} died"
    case WaitLog(creature) =>
      s"${creature.name} waited"
    case ShoveLog(shover, target, true) =>
      s"${shover.name} shoved ${target.name} back"
    case ShoveLog(shover, target, false) =>
      s"${shover.name} couldn't move ${target.name}"
