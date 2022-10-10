package amink.rpg.battle.model

enum Species(
    val name: String,
    val team: Team,
    val maxHp: Int,
    val size: Size,
    val strength: Int,
    val actionCost: Int,
    val weapon: Weapon
):

  case Goon(
      override val name: String = "goon",
      override val team: Team = Team.Players,
      override val maxHp: Int = 100,
      override val size: Size = Size.Medium,
      override val strength: Int = 10,
      override val actionCost: Int = 100,
      override val weapon: Weapon
  ) extends Species(name, team, maxHp, size, strength, actionCost, weapon)

  case Fighter(
      override val weapon: Weapon
  ) extends Species(
        name = "fighter",
        team = Team.Players,
        size = Size.Medium,
        maxHp = 10,
        strength = 2,
        actionCost = 100,
        weapon
      )

  case Goblin
      extends Species(
        name = "goblin",
        team = Team.Monsters,
        size = Size.Medium,
        maxHp = 5,
        strength = 1,
        actionCost = 50,
        weapon = Weapon(
          name = "knife",
          range = Range.Close,
          strengthMultiplier = 1,
          damageBonus = 0,
          actionCostMultiplier = 1
        )
      )

  case Ogre
      extends Species(
        name = "ogre",
        team = Team.Monsters,
        size = Size.Big,
        maxHp = 20,
        strength = 5,
        actionCost = 150,
        weapon = Weapon(
          name = "club",
          range = Range.Close,
          strengthMultiplier = 1,
          damageBonus = 0,
          actionCostMultiplier = 1
        )
      )

object Species:
  val goblinBow = Weapon(
    name = "bow",
    range = Range.Ranged,
    strengthMultiplier = 0,
    damageBonus = 3,
    actionCostMultiplier = 2
  )
