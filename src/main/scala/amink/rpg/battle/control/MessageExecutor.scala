package amink.rpg.battle.control

import amink.rpg.engine.Command
import amink.rpg.battle.model.*
import amink.canvasui.Sprite.apply

object MessageExecutor:
  import Command.*
  import Message.*
  import State.*

  def execute(state: State, message: Message): (State, Command[Message]) =
    state match
      case s: LoadingSprites     => loadingSprites(s, message)
      case s: SelectingAction    => selectingAction(s, message)
      case s: SelectingDirection => selectingDirection(s, message)
      case s: SelectingMonster   => selectingMonster(s, message)
      case s: MonsterActing      => monsterActing(s, message)
      case s: ExecutingAction    => executingAction(s, message)
      case s: Logging            => logging(s, message)
      case s: Won                => (s, Noop)
      case s: Lost               => (s, Noop)

  private def loadingSprites(
      s: LoadingSprites,
      msg: Message
  ): (State, Command[Message]) = msg match
    case Start =>
      s.spriteSources.headOption match
        case Some(name, src) =>
          val sprite = s.sprites(name)
          val cmd = LoadSpriteAndSend(sprite, src, SpriteLoaded(sprite))
          (s, cmd)
        case None => transitionToNextTurn(s)

    case SpriteLoaded(sprite) =>
      val sprites = s.sprites + (sprite.name -> sprite)
      val sources = s.spriteSources - sprite.name
      val state = s.copy(sprites = sprites, spriteSources = sources)
      (state, Send(Start))

    case _ => (s, Noop)

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

  private def monsterActing(
      s: MonsterActing,
      msg: Message
  ): (State, Command[Message]) =
    msg match
      case MonsterAct =>
        val (seed, action) =
          MonsterBehavior.selectAction(s.creatureMap, s.id, s.seed)

        val state = s.copy(seed = seed)
        transitionToExecutingAction(state, action)

      case _ => (s, Noop)

  private def executingAction(
      s: ExecutingAction,
      msg: Message
  ): (State, Command[Message]) =
    msg match
      case ExecuteAction =>
        val result = ActionExecutor.execute(s.creatureMap, s.action, s.seed)
        val state =
          Logging(result.seed, result.creatureMap, s.sprites, result.logs)
        (state, Render)

      case _ => (s, Noop)

  private def logging(
      s: Logging,
      msg: Message
  ): (State, Command[Message]) =
    msg match
      case Confirm => transitionToNextTurn(s)
      case _       => (s, Noop)

  private def transitionToNextTurn(
      s: State
  ): (State, Command[Message]) =
    val lost = s.creatureMap.players.isEmpty
    val won = s.creatureMap.monsters.isEmpty
    lazy val next = s.creatureMap.queue.head
    lazy val playerTurn = next.species.team == Team.Players

    if lost then transitionToLost(s)
    else if won then transitionToWon(s)
    else if playerTurn then transitionToSelectingAction(s, next.id)
    else transitionToMonsterActing(s, next.id)

  private def transitionToSelectingAction(
      s: State,
      id: Id
  ): (State, Command[Message]) =
    val targets = s.creatureMap.validWeaponTargets(id).nonEmpty
    val directions = s.creatureMap.validDirections(id).nonEmpty
    val shoveTargets = s.creatureMap.validTargets(id, Range.Close).nonEmpty

    val attackOpt = if targets then Some(PlayerAction.Attack) else None
    val moveOpt = if directions then Some(PlayerAction.Move) else None
    val shoveOpt = if shoveTargets then Some(PlayerAction.Shove) else None
    val waitOpt = Some(PlayerAction.Wait)
    val actions = List(attackOpt, moveOpt, shoveOpt, waitOpt).flatten

    val state =
      SelectingAction(s.seed, s.creatureMap, s.sprites, id, actions, 0)
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
    val state =
      SelectingMonster(s.seed, s.creatureMap, s.sprites, id, a, monsters, 0)
    (state, Render)

  private def transitionToSelectingDirection(
      s: State,
      a: PlayerAction,
      id: Id
  ): (State, Command[Message]) =
    val dirs = s.creatureMap.validDirections(id)
    val state =
      SelectingDirection(s.seed, s.creatureMap, s.sprites, id, a, dirs, 0)
    (state, Render)

  private def transitionToExecutingAction(
      s: State,
      a: Action
  ): (State, Command[Message]) =
    val state = ExecutingAction(s.seed, s.creatureMap, s.sprites, a)
    (state, Send(ExecuteAction))

  private def transitionToMonsterActing(
      s: State,
      id: Id
  ): (State, Command[Message]) =
    val state = MonsterActing(s.seed, s.creatureMap, s.sprites, id)
    (state, Send(MonsterAct))

  private def transitionToLost(s: State): (State, Command[Message]) =
    val state = Lost(s.seed, s.creatureMap, s.sprites)
    (state, Render)

  private def transitionToWon(s: State): (State, Command[Message]) =
    val state = Won(s.seed, s.creatureMap, s.sprites)
    (state, Render)
