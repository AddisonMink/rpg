package amink.rpg.engine

enum Command[+Message]:
  case Noop
  case Render
  case Send(a: Message)
  case DelayedSend(a: Message, millis: Int)
