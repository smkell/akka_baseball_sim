package com.github.smkell.akka.baseball

import akka.actor.FSM.{CurrentState, SubscribeTransitionCallBack}
import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import com.github.smkell.akka.baseball.BaseballGame._
import com.github.smkell.akka.baseball.BaseballGameProtocol._
import org.scalatest.{FunSpecLike, MustMatchers}

/**
  * Created by Sean on 6/3/2016.
  */
class BaseballGameSpec extends TestKit(ActorSystem("baseball-system")) with MustMatchers with FunSpecLike with ImplicitSender {
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
  }
}
