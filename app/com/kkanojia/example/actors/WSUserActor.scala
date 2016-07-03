package com.kkanojia.example.actors

import javax.inject._
import scala.concurrent.duration._

import akka.actor._
import akka.event.LoggingReceive
import akka.util.Timeout
import com.google.inject.assistedinject.Assisted
import TradeAggregateViewActor.WatchTrades
import TradeActor.{TradeCreated, TradeUpdated}
import org.json4s.DefaultFormats
import org.json4s.ext.{JavaTypesSerializers, JodaTimeSerializers}
import org.json4s.jackson.Serialization._

import play.api.Configuration
import play.api.libs.concurrent.InjectedActorSupport

class WSUserActor @Inject()(@Assisted out: ActorRef,
                            configuration: Configuration) extends Actor with ActorLogging {

  implicit val timeout = Timeout(15 seconds)
  implicit val formats = DefaultFormats ++ JavaTypesSerializers.all ++ JodaTimeSerializers.all
  val tradeViewActor = context.actorSelection("/user/" + TradeAggregateViewActor.NAME)

  override def preStart(): Unit = {
    super.preStart()
    tradeViewActor ! WatchTrades
  }

  override def receive: Receive = LoggingReceive {

    case TradeCreated(trade) =>
      val tradeCreateMessage = write(trade)
      out ! tradeCreateMessage

    case TradeUpdated(trade) =>
      val tradeUpdateMessage = write(trade)
      out ! tradeUpdateMessage

  }
}

class WSUserParentActor @Inject()(childFactory: WSUserActor.Factory)
  extends Actor with InjectedActorSupport with ActorLogging {

  import WSUserParentActor._

  override def receive: Receive = LoggingReceive {
    case Create(id, out) =>
      val child: ActorRef = injectedChild(childFactory(out), s"ws-userActor-$id")
      sender() ! child
  }
}

object WSUserParentActor {

  case class Create(id: String, out: ActorRef)

}

object WSUserActor {

  trait Factory {
    // Corresponds to the @Assisted parameters defined in the constructor
    def apply(out: ActorRef): Actor
  }

}

