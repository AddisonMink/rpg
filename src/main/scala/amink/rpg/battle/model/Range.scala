package amink.rpg.battle.model

enum Range:
  case Close
  case Reach
  case Ranged

  def canTarget(row: Row): List[Row] = (this, row) match
    case (Close, Row.Front) => List(Row.Front)
    case (Reach, Row.Front) => List(Row.Back)
    case (Reach, Row.Back)  => List(Row.Front)
    case (Ranged, Row.Back) => List(Row.Front, Row.Back)
    case _                  => Nil
