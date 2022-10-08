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
