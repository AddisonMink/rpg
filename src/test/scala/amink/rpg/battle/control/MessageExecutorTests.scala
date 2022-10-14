package amink.rpg.battle.control

import monocle.syntax.all.*

import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

import amink.canvasui.Sprite

import amink.rpg.battle.model.*
import amink.rpg.engine.Command
import amink.rpg.util.Seed

class MessageExecutorTests extends AnyFlatSpecLike with Matchers:
  import Command.*
  import Message.*
  import State.*

  private val seed = Seed.Cycle(Nil)
  private val playerActions = PlayerAction.values.toList
  private val directions = Direction.values.toList
  private val fighter = Species.Fighter(Weapon("", Range.Close, 1, 0, 1))

  behavior of "loadingSprites"

  it should "on Start, load the next sprite if there are sources remaining" in {
    val sprite = Sprite("test", null, 0, 0, 0, 0, 0, 0)
    val sprites = Map("test" -> sprite)
    val sources = Map("test" -> "path/to/img")
    val state = LoadingSprites(seed, Map(), sprites, sources)

    val (actual, cmd) = MessageExecutor.execute(state, Start)

    actual shouldBe state
    cmd shouldBe LoadSpriteAndSend(sprite, "path/to/img", SpriteLoaded(sprite))
  }

  it should "on Start, transition to the next turn if there are no sources remaining" in {
    val player = Creature.make(0, "1", fighter, Row.Front)
    val creatureMap = Map(0 -> player)
    val state = LoadingSprites(seed, creatureMap, Map(), Map())

    val expected = Won(seed, creatureMap, Map())

    val (actual, cmd) = MessageExecutor.execute(state, Start)

    actual shouldBe expected
    cmd shouldBe Render
  }

  it should "on SpriteLoaded, remove source and emit Start" in {
    val sprite = Sprite("test", null, 0, 0, 0, 0, 0, 0)
    val sprites = Map("test" -> sprite)
    val sources = Map("test" -> "path/to/img")
    val state: LoadingSprites = LoadingSprites(seed, Map(), sprites, sources)

    val expected = state.copy(spriteSources = state.spriteSources - "test")

    val (actual, cmd) = MessageExecutor.execute(state, SpriteLoaded(sprite))

    actual shouldBe expected
    cmd shouldBe Send(Start)
  }

  behavior of "selectingAction"

  it should "on Up, scroll actions up" in {
    val state: SelectingAction =
      SelectingAction(seed, Map(), Map(), 0, playerActions, 0)

    val expected1 = state.copy(index = playerActions.length - 1)
    val expected2 = state.copy(index = playerActions.length - 2)

    val (actual1, cmd1) = MessageExecutor.execute(state, Up)
    val (actual2, cmd2) = MessageExecutor.execute(actual1, Up)

    actual1 shouldBe expected1
    cmd1 shouldBe Render
    actual2 shouldBe expected2
    cmd2 shouldBe Render
  }

  it should "on Down, scroll actions down" in {
    val state: SelectingAction =
      SelectingAction(
        seed,
        Map(),
        Map(),
        0,
        playerActions,
        playerActions.length - 1
      )

    val expected1 = state.copy(index = 0)
    val expected2 = state.copy(index = 1)

    val (actual1, cmd1) = MessageExecutor.execute(state, Down)
    val (actual2, cmd2) = MessageExecutor.execute(actual1, Down)

    actual1 shouldBe expected1
    cmd1 shouldBe Render
    actual2 shouldBe expected2
    cmd2 shouldBe Render
  }

  it should "on Confirm with Wait, transition to ExecutingAction" in {
    val index = playerActions.indexOf(PlayerAction.Wait)

    val state: SelectingAction =
      SelectingAction(seed, Map(), Map(), 0, playerActions, index)

    val expected = ExecutingAction(seed, Map(), Map(), Action.Wait(0))

    val (actual, cmd) = MessageExecutor.execute(state, Confirm)

    actual shouldBe expected
    cmd shouldBe Send(ExecuteAction)
  }

  it should "on Confirm with Move, transition to SelectingDirection" in {
    val index = playerActions.indexOf(PlayerAction.Move)
    val creature = Creature.make(0, "1", Species.Goblin, Row.Front)

    val state: SelectingAction =
      SelectingAction(seed, Map(0 -> creature), Map(), 0, playerActions, index)

    val expected: SelectingDirection =
      SelectingDirection(
        seed,
        Map(0 -> creature),
        Map(),
        0,
        PlayerAction.Move,
        List(Direction.Forward, Direction.Back),
        0
      )

    val (actual, cmd) = MessageExecutor.execute(state, Confirm)

    actual shouldBe expected
    cmd shouldBe Render
  }

  it should "on Confirm and Shove, transition to SelectingMonster" in {
    val index = playerActions.indexOf(PlayerAction.Shove)
    val player = Creature.make(0, "1", fighter, Row.Front)
    val monster = Creature.make(1, "1", Species.Goblin, Row.Front)
    val creatureMap = Map(0 -> player, 1 -> monster)
    val state: SelectingAction =
      SelectingAction(seed, creatureMap, Map(), 0, playerActions, index)

    val expected: SelectingMonster =
      SelectingMonster(
        seed,
        creatureMap,
        Map(),
        0,
        PlayerAction.Shove,
        List(1),
        0
      )

    val (actual, cmd) = MessageExecutor.execute(state, Confirm)

    actual shouldBe expected
    cmd shouldBe Render
  }

  it should "on Confirm and Attack, transition to MonsterSelecting" in {
    val index = playerActions.indexOf(PlayerAction.Attack)
    val player = Creature.make(0, "1", fighter, Row.Front)
    val monster = Creature.make(1, "1", Species.Goblin, Row.Front)
    val creatureMap = Map(0 -> player, 1 -> monster)
    val state: SelectingAction =
      SelectingAction(seed, creatureMap, Map(), 0, playerActions, index)

    val expected: SelectingMonster =
      SelectingMonster(
        seed,
        creatureMap,
        Map(),
        0,
        PlayerAction.Attack,
        List(1),
        0
      )

    val (actual, cmd) = MessageExecutor.execute(state, Confirm)

    actual shouldBe expected
    cmd shouldBe Render
  }

  behavior of "selectingDirection"

  it should "on Confirm, transition to ExecutingAction" in {
    val index = directions.indexOf(Direction.Forward)

    val state =
      SelectingDirection(
        seed,
        Map(),
        Map(),
        0,
        PlayerAction.Move,
        directions,
        index
      )

    val action = Action.Move(0, Direction.Forward)
    val expected = ExecutingAction(seed, Map(), Map(), action)

    val (actual, cmd) = MessageExecutor.execute(state, Confirm)

    actual shouldBe expected
    cmd shouldBe Send(ExecuteAction)
  }

  it should "on Cancel, transition to SelectingAction" in {
    val state =
      SelectingDirection(seed, Map(), Map(), 0, PlayerAction.Move, Nil, 0)

    val expected = SelectingAction(seed, Map(), Map(), 0, playerActions, 0)

    val (actual, cmd) = MessageExecutor.execute(state, Cancel)

    actual shouldBe expected
    cmd shouldBe Render
  }

  behavior of "monsterActing"

  it should "on MonsterAct select an action for the monster and transition to ExecutingAction" in {
    val player = Creature.make(0, "1", fighter, Row.Front)
    val monster = Creature.make(1, "1", Species.Goblin, Row.Back)
    val creatureMap = Map(0 -> player, 1 -> monster)
    val state: MonsterActing = MonsterActing(seed, creatureMap, Map(), 1)

    val action = Action.Attack(1, 0, Species.goblinBow)
    val expected: ExecutingAction =
      ExecutingAction(seed, creatureMap, Map(), action)

    val (actual, cmd) = MessageExecutor.execute(state, MonsterAct)

    actual shouldBe expected
    cmd shouldBe Send(ExecuteAction)
  }

  behavior of "executingAction"

  it should "on ExecuteAction execute teh action and transition to Logging" in {
    val player = Creature.make(0, "1", fighter, Row.Front)
    val creatureMap = Map(0 -> player)
    val action = Action.Wait(0)
    val state: ExecutingAction =
      ExecutingAction(seed, creatureMap, Map(), action)

    val expectedCMap = state.creatureMap
      .focus(_.index(0).nextActionAt)
      .modify(_ + 25)

    val expected: Logging =
      Logging(seed, expectedCMap, Map(), List(Log.WaitLog(player)))

    val (actual, cmd) = MessageExecutor.execute(state, ExecuteAction)

    actual shouldBe expected
    cmd shouldBe Render
  }

  behavior of "logging"

  it should "on Confirm transition to SelectingAction if the next turn is a player turn." in {
    val player = Creature.make(0, "1", fighter, Row.Front)
    val monster =
      Creature.make(1, "1", Species.Goblin, Row.Front).copy(nextActionAt = 1000)
    val creatureMap = Map(0 -> player, 1 -> monster)
    val state: Logging = Logging(seed, creatureMap, Map(), Nil)

    val expected: SelectingAction =
      SelectingAction(seed, creatureMap, Map(), 0, playerActions, 0)

    val (actual, cmd) = MessageExecutor.execute(state, Confirm)

    actual shouldBe expected
    cmd shouldBe Render
  }

  it should "on Confirm transition to MonsterActing if the next run is a monster turn." in {
    val player =
      Creature.make(0, "1", fighter, Row.Front).copy(nextActionAt = 1000)
    val monster = Creature.make(1, "1", Species.Goblin, Row.Front)
    val creatureMap = Map(0 -> player, 1 -> monster)
    val state: Logging = Logging(seed, creatureMap, Map(), Nil)

    val expected: MonsterActing = MonsterActing(seed, creatureMap, Map(), 1)

    val (actual, cmd) = MessageExecutor.execute(state, Confirm)

    actual shouldBe expected
    cmd shouldBe Send(MonsterAct)
  }

  it should "on Confirm transition to Lost if there are no living players" in {
    val monster = Creature.make(0, "1", Species.Goblin, Row.Back)
    val creatureMap = Map(0 -> monster)
    val action = Action.Wait(0)
    val state: Logging = Logging(seed, creatureMap, Map(), Nil)

    val expected: Lost = Lost(seed, creatureMap, Map())

    val (actual, cmd) = MessageExecutor.execute(state, Confirm)

    actual shouldBe expected
    cmd shouldBe Render
  }

  it should "on ExecuteAction execute the action and transition to Won if there are no living monsters" in {
    val player = Creature.make(0, "1", fighter, Row.Front)
    val creatureMap = Map(0 -> player)
    val action = Action.Wait(0)
    val state: Logging = Logging(seed, creatureMap, Map(), Nil)

    val expected: Won = Won(seed, creatureMap, Map())

    val (actual, cmd) = MessageExecutor.execute(state, Confirm)

    actual shouldBe expected
    cmd shouldBe Render
  }
