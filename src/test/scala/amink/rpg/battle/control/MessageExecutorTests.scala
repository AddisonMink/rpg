package amink.rpg.battle.control

import monocle.syntax.all.*

import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

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

  behavior of "selectingAction"

  it should "on Up, scroll actions up" in {
    val state: SelectingAction =
      SelectingAction(seed, Map(), 0, playerActions, 0)

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
      SelectingAction(seed, Map(), 0, playerActions, playerActions.length - 1)

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
      SelectingAction(seed, Map(), 0, playerActions, index)

    val expected = ExecutingAction(seed, Map(), Action.Wait(0))

    val (actual, cmd) = MessageExecutor.execute(state, Confirm)

    actual shouldBe expected
    cmd shouldBe Send(ExecuteAction)
  }

  it should "on Confirm with Move, transition to SelectingDirection" in {
    val index = playerActions.indexOf(PlayerAction.Move)
    val creature = Creature.make(0, "1", Species.Goblin, Row.Front)

    val state: SelectingAction =
      SelectingAction(seed, Map(0 -> creature), 0, playerActions, index)

    val expected: SelectingDirection =
      SelectingDirection(
        seed,
        Map(0 -> creature),
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
      SelectingAction(seed, creatureMap, 0, playerActions, index)

    val expected: SelectingMonster =
      SelectingMonster(seed, creatureMap, 0, PlayerAction.Shove, List(1), 0)

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
      SelectingAction(seed, creatureMap, 0, playerActions, index)

    val expected: SelectingMonster =
      SelectingMonster(seed, creatureMap, 0, PlayerAction.Attack, List(1), 0)

    val (actual, cmd) = MessageExecutor.execute(state, Confirm)

    actual shouldBe expected
    cmd shouldBe Render
  }

  behavior of "selectingDirection"

  it should "on Confirm, transition to ExecutingAction" in {
    val index = directions.indexOf(Direction.Forward)

    val state =
      SelectingDirection(seed, Map(), 0, PlayerAction.Move, directions, index)

    val action = Action.Move(0, Direction.Forward)
    val expected = ExecutingAction(seed, Map(), action)

    val (actual, cmd) = MessageExecutor.execute(state, Confirm)

    actual shouldBe expected
    cmd shouldBe Send(ExecuteAction)
  }

  it should "on Cancel, transition to SelectingAction" in {
    val state = SelectingDirection(seed, Map(), 0, PlayerAction.Move, Nil, 0)

    val expected = SelectingAction(seed, Map(), 0, playerActions, 0)

    val (actual, cmd) = MessageExecutor.execute(state, Cancel)

    actual shouldBe expected
    cmd shouldBe Render
  }

  behavior of "monsterActing"

  it should "on MonsterAct select an action for the monster and transition to ExecutingAction" in {
    val player = Creature.make(0, "1", fighter, Row.Front)
    val monster = Creature.make(1, "1", Species.Goblin, Row.Back)
    val creatureMap = Map(0 -> player, 1 -> monster)
    val state: MonsterActing = MonsterActing(seed, creatureMap, 1)

    val action = Action.Attack(1, 0, Species.goblinBow)
    val expected: ExecutingAction = ExecutingAction(seed, creatureMap, action)

    val (actual, cmd) = MessageExecutor.execute(state, MonsterAct)

    actual shouldBe expected
    cmd shouldBe Send(ExecuteAction)
  }

  behavior of "executingAction"

  it should "on ExecuteAction execute the action and transition to SelectingAction if the next turn is a player turn." in {
    val player = Creature.make(0, "1", fighter, Row.Front)

    val monster = Creature
      .make(1, "1", Species.Goblin, Row.Back)
      .copy(nextActionAt = 1000)

    val creatureMap = Map(0 -> player, 1 -> monster)
    val action = Action.Wait(0)
    val state: ExecutingAction = ExecutingAction(seed, creatureMap, action)

    val expectedCMap = state.creatureMap
      .focus(_.index(0).nextActionAt)
      .modify(_ + 25)

    val expected: SelectingAction =
      SelectingAction(seed, expectedCMap, 0, playerActions, 0)

    val (actual, cmd) = MessageExecutor.execute(state, ExecuteAction)

    actual shouldBe expected
    cmd shouldBe Render
  }

  it should "on ExecuteAction execute the action and transition to MonsterActing if the next run is a monster turn." in {
    val player = Creature.make(0, "1", fighter, Row.Front)
    val monster = Creature.make(1, "1", Species.Goblin, Row.Back)
    val creatureMap = Map(0 -> player, 1 -> monster)
    val action = Action.Wait(0)
    val state: ExecutingAction = ExecutingAction(seed, creatureMap, action)

    val expectedCMap = state.creatureMap
      .focus(_.index(0).nextActionAt)
      .modify(_ + 25)

    val expected: MonsterActing = MonsterActing(seed, expectedCMap, 1)

    val (actual, cmd) = MessageExecutor.execute(state, ExecuteAction)

    actual shouldBe expected
    cmd shouldBe Send(MonsterAct)
  }

  it should "on ExecuteAction execute the action and transition to Lost if there are no living players" in {
    val monster = Creature.make(0, "1", Species.Goblin, Row.Back)
    val creatureMap = Map(0 -> monster)
    val action = Action.Wait(0)
    val state: ExecutingAction = ExecutingAction(seed, creatureMap, action)

    val expectedCMap = state.creatureMap
      .focus(_.index(0).nextActionAt)
      .modify(_ + 12)

    val expected: Lost = Lost(seed, expectedCMap)

    val (actual, cmd) = MessageExecutor.execute(state, ExecuteAction)

    actual shouldBe expected
    cmd shouldBe Render
  }

  it should "on ExecuteAction execute the action and transition to Won if there are no living monsters" in {
    val player = Creature.make(0, "1", fighter, Row.Front)
    val creatureMap = Map(0 -> player)
    val action = Action.Wait(0)
    val state: ExecutingAction = ExecutingAction(seed, creatureMap, action)

    val expectedCMap = state.creatureMap
      .focus(_.index(0).nextActionAt)
      .modify(_ + 25)

    val expected: Won = Won(seed, expectedCMap)

    val (actual, cmd) = MessageExecutor.execute(state, ExecuteAction)

    actual shouldBe expected
    cmd shouldBe Render
  }
