package amink.rpg.battle.model

enum Message:

  // Inputs
  case Up
  case Down
  case Left
  case Right
  case Confirm
  case Cancel

  // State Transitions
  case MonsterAct
  case ExecuteAction
