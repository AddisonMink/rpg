package amink.rpg.battle.model

final case class Weapon(
    name: String,
    range: Range,
    strengthMultiplier: Double,
    damageBonus: Int,
    actionCostMultiplier: Double
)
