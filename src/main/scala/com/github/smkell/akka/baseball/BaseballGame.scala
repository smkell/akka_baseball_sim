package com.github.smkell.akka.baseball

import akka.actor.FSM
import com.github.smkell.akka.baseball.BaseballGame._
import com.github.smkell.akka.baseball.BaseballGameProtocol._

object BaseballGame {

  sealed trait GameState

  case class GameData(currentCount: Count, currentInning: Inning, currentScore: Score, currentOuts: Int)

  case object NoRunners extends GameState

}

object BaseballGameProtocol {

  sealed trait Side

  case class Count(balls: Int, strikes: Int)

  case class Inning(inning: Int, side: Side)

  case class Score(awayScore: Int, homeScore: Int)

  case object Top extends Side

  case object Bottom extends Side

  case object GetCount

  case object GetInning

  case object GetOuts

  case object GetScore

}

/**
  * Created by Sean on 6/3/2016.
  */
class BaseballGame extends FSM[GameState, GameData] {
  startWith(
    NoRunners,
    GameData(
      currentCount = Count(0, 0),
      currentInning = Inning(1, Top),
      currentScore = Score(0, 0),
      currentOuts = 0
    )
  )

  when(NoRunners) {
    case Event(GetCount, _) => sender ! stateData.currentCount; stay()
  }

  whenUnhandled {
    case Event(GetCount, _) => sender ! stateData.currentCount; stay()
    case Event(GetInning, _) => sender ! stateData.currentInning; stay()
    case Event(GetOuts, _) => sender ! stateData.currentOuts; stay()
    case Event(GetScore, _) => sender ! stateData.currentScore; stay()
  }
}
