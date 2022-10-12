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
      case s: SelectingDirection => selectingDirection(s, message)
      case s: SelectingMonster   => selectingMonster(s, message)
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

  private def selectingDirection(
      s: SelectingDirection,
      msg: Message
  ): (State, Command[Message]) =
    msg match
      case Up   => (s.selectPrev, Render)
      case Down => (s.selectNext, Render)

      case Confirm =>
        val dir = s.directions(s.index)
        val action = s.action.toDirectionalAction(s.id, dir)
        transitionToExecutingAction(s, action)

      case Cancel => transitionToSelectingAction(s, s.id)
      case _      => (s, Noop)

  private def selectingMonster(
      s: SelectingMonster,
      msg: Message
  ): (State, Command[Message]) =
    msg match
      case Left   => (s.selectPrev, Render)
      case Right  => (s.selectNext, Render)
      case Cancel => transitionToSelectingAction(s, s.id)

      case Confirm =>
        val monsterId = s.monsters(s.index)
        val action = s.action.toMonsterTargetingAction(s.id, monsterId)
        transitionToExecutingAction(s, action)

      case _ => (s, Noop)

  private def transitionToSelectingAction(
      s: State,
      id: Id
  ): (State, Command[Message]) =
    val actions = PlayerAction.values.toList
    val state = SelectingAction(s.seed, s.creatureMap, id, actions, 0)
    (state, Render)

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
