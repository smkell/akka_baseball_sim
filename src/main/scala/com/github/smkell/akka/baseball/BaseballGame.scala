package com.github.smkell.akka.baseball

import akka.actor.FSM
import com.github.smkell.akka.baseball.BaseballGame._
import com.github.smkell.akka.baseball.BaseballGameProtocol._

/** Companion object for the BaseballGame class.
  *
  */
object BaseballGame {

  sealed trait GameState

  case class GameData(currentCount: Count, currentInning: Inning, currentScore: Score, currentOuts: Int)

  case object NoRunners extends GameState

}

/** Protocol defining the public interface of the BaseballGame actor.
  *
  */
object BaseballGameProtocol {

  sealed trait Side
  case object Top extends Side
  case object Bottom extends Side

  sealed trait PitchType
  case object Ball extends PitchType
  case object Strike extends PitchType

  case class Count(balls: Int, strikes: Int)
  case class Inning(inning: Int, side: Side)
  case class Score(awayScore: Int, homeScore: Int)

  // Events
  case object GetCount
  case object GetInning
  case object GetOuts
  case object GetScore

  case class ThrowPitch(pitch: PitchType)
}

/** FSM for simulating a baseball game.
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
    case Event(ThrowPitch(Strike), GameData(Count(balls, strikes), _, _, _)) => {
      stay() using stateData.copy(currentCount = Count(balls = balls, strikes= strikes + 1))
    }
  }

  whenUnhandled {
    case Event(GetCount, _) => sender ! stateData.currentCount; stay()
    case Event(GetInning, _) => sender ! stateData.currentInning; stay()
    case Event(GetOuts, _) => sender ! stateData.currentOuts; stay()
    case Event(GetScore, _) => sender ! stateData.currentScore; stay()
  }
}
