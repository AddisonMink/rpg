package amink.rpg.battle.model

type Id = Int

final case class Creature(
    id: Id,
    nameSuffix: String,
    species: Species,
    hp: Int,
    row: Row,
    nextActionAt: Int,
    state: CreatureState
):
  def name: String = s"${species.name} $nameSuffix"

type CreatureMap = Map[Id, Creature]

extension (c: CreatureMap)
  def creatures: List[Creature] = c.values.toList.filter(_.state.isAlive)
  def players: List[Creature] = creatures.filter(_.species.team.isPlayer)
  def monsters: List[Creature] = creatures.filter(_.species.team.isMonster)

  def team(team: Team): List[Creature] =
    creatures.filter(_.species.team == team)

  def validTargets(id: Id, range: Range): List[Id] =
    val creature = c(id)
    val rows = range.canTarget(creature.row)
    val oppositeTeam = team(creature.species.team.opposite)

    oppositeTeam
      .filter(ctr => rows.contains(ctr.row))
      .map(_.id)

  def validWeaponTargets(id: Id): List[Id] =
    val range = c(id).species.weapon.range
    validTargets(id, range)

  def validDirections(id: Id): List[Direction] =
    val creature = c(id)
    creature.row match
      case Row.Front =>
        val isSpace = c
          .team(creature.species.team.opposite)
          .forall(_.row == Row.Back)

        if isSpace then List(Direction.Forward, Direction.Back)
        else List(Direction.Back)

      case Row.Back => List(Direction.Forward)

object Creature:
  def make(id: Id, nameSuffix: String, species: Species, row: Row): Creature =
    Creature(
      id,
      nameSuffix,
      species,
      hp = species.maxHp,
      row,
      nextActionAt = species.actionCost,
      state = CreatureState.Alive
    )
