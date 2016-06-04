package com.github.smkell.akka.baseball

import akka.actor.FSM.{CurrentState, SubscribeTransitionCallBack, Transition}
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import com.github.smkell.akka.baseball.BaseballGame._
import com.github.smkell.akka.baseball.BaseballGameProtocol._
import org.scalatest.{FunSpecLike, MustMatchers}

/**
  * Created by Sean on 6/3/2016.
  */
class BaseballGameSpec extends TestKit(ActorSystem("baseball-system")) with MustMatchers with FunSpecLike with ImplicitSender {
  def walkBatter(game: ActorRef): Unit = {
    for (i <- 1 to 4) {
      game ! ThrowPitch(Ball)
    }
  }

  def strikeoutBatter(game: ActorRef): Unit = {
    for(i <- 1 to 3) {
      game ! ThrowPitch(Strike)
    }
  }

  describe("A baseball game") {

    it("should allow accessing the current count") {
      val game = TestActorRef(Props(new BaseballGame()))
      game ! GetCount
      expectMsg(Count(0, 0))
    }

    it("should allow accessing the current inning") {
      val game = TestActorRef(Props(new BaseballGame()))
      game ! GetInning
      expectMsg(Inning(1, Top))
    }

    it("should allow accessing the current score") {
      val game = TestActorRef(Props(new BaseballGame()))
      game ! GetScore
      expectMsg(Score(0, 0))
    }

    it("should allow accessing the current outs") {
      val game = TestActorRef(Props(new BaseballGame()))
      game ! GetOuts
      expectMsg(0)
    }

    it("should begin with no runners on") {
      val game = TestActorRef(Props(new BaseballGame()))
      game ! SubscribeTransitionCallBack(testActor)
      expectMsg(CurrentState(game, NoRunners))
    }

    describe("when a strike is thrown") {
      val game = TestActorRef(Props(new BaseballGame()))
      game ! ThrowPitch(Strike)

      describe("the count") {
        it("should be 0-1") {
          game ! GetCount
          expectMsg(Count(balls = 0, strikes = 1))
        }
      }
    }

    describe("when three strikes are thrown in a row") {
      describe("the count") {
        it("should be 0-0") {
          val game = TestActorRef(Props(new BaseballGame()))
          strikeoutBatter(game)
          game ! GetCount
          expectMsg(Count(0, 0))
        }
      }

      describe("the number of outs") {
        it("should be 1") {
          val game = TestActorRef(Props(new BaseballGame))
          strikeoutBatter(game)
          game ! GetOuts
          expectMsg(1)
        }
      }
    }

    describe("when a ball is thrown") {
      val game = TestActorRef(Props(new BaseballGame()))
      game ! ThrowPitch(Ball)

      describe("the count") {
        it("should be 1-0") {
          game ! GetCount
          expectMsg(Count(balls = 1, strikes = 0))
        }
      }
    }

    describe("when four balls are thrown in a row") {
      it("there should be a runner on first") {
        val game = TestActorRef(Props(new BaseballGame()))
        game ! SubscribeTransitionCallBack(testActor)
        expectMsg(CurrentState(game, NoRunners))
        walkBatter(game)
        expectMsg(Transition(game, NoRunners, RunnerOn1st))
      }

      describe("the count") {
        it("should be 0-0") {
          val game = TestActorRef(Props(new BaseballGame()))
          walkBatter(game)
          game ! GetCount
          expectMsg(Count(0, 0))
        }
      }
    }
  }
}
