package amink.rpg.battle.model

enum CreatureState:
  case Alive
  case Dead

  def isAlive: Boolean = this == Alive
  def isDead: Boolean = this == Dead