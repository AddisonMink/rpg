package amink.rpg.battle.control

import amink.rpg.engine.Command
import amink.rpg.battle.model.*

object MessageExecutor:
  import Command.*
  import Message.*
  import State.*

  def execute(state: State, message: Message): (State, Command[Message]) =
    state match
      case s: SelectingAction    => selectingAction(s, message)
      case s: SelectingDirection => ???
      case s: SelectingMonster   => ???
      case s: MonsterActing      => ???
      case s: ExecutingAction    => ???
      case s: Logging            => ???
      case s: Won                => ???
      case s: Lost               => ???

  private def selectingAction(
      s: SelectingAction,
      msg: Message
  ): (State, Command[Message]) = msg match
    case Up   => (s.selectPrev, Render)
    case Down => (s.selectNext, Render)

    case Confirm =>
      val playerAction = s.actions(s.index)
      playerAction match
        case PlayerAction.Attack =>
          transitionToSelectingMonster(s, playerAction, s.id)

        case PlayerAction.Move =>
          transitionToSelectingDirection(s, playerAction, s.id)

        case PlayerAction.Shove =>
          transitionToSelectingMonster(s, playerAction, s.id, Some(Range.Close))

        case PlayerAction.Wait =>
          val action = playerAction.toSelfTargetingAction(s.id)
          transitionToExecutingAction(s, action)

    case _ => (s, Noop)

  private def transitionToSelectingMonster(
      s: State,
      a: PlayerAction,
      id: Id,
      range: Option[Range] = None
  ): (State, Command[Message]) =
    val monsters = range match
      case Some(r) => s.creatureMap.validTargets(id, r)
      case None    => s.creatureMap.validWeaponTargets(id)
    val state = SelectingMonster(s.seed, s.creatureMap, id, a, monsters, 0)
    (state, Render)

  private def transitionToSelectingDirection(
      s: State,
      a: PlayerAction,
      id: Id
  ): (State, Command[Message]) =
    val dirs = s.creatureMap.validDirections(id)
    val state = SelectingDirection(s.seed, s.creatureMap, id, a, dirs, 0)
    (state, Render)

  private def transitionToExecutingAction(
      s: State,
      a: Action
  ): (State, Command[Message]) =
    val state = ExecutingAction(s.seed, s.creatureMap, a)
    (state, Send(ExecuteAction))
