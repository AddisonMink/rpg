package amink.rpg.battle.model

enum Team:
  case Players
  case Monsters

  def isPlayer: Boolean = this == Players
  def isMonster: Boolean = this == Monsters