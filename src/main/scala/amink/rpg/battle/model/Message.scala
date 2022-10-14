package amink.rpg.battle.model

import amink.canvasui.Sprite

enum Message:

  // Inputs
  case Up
  case Down
  case Left
  case Right
  case Confirm
  case Cancel

  // State Transitions
  case SpriteLoaded(sprite: Sprite)
  case MonsterAct
  case ExecuteAction
