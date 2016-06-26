package com.kkanojia.rpe.modules

import akka.actor.{ActorSystem, Props}
import com.google.inject.{Inject, Singleton}
import com.kkanojia.rpe.actors.{CumulativeTradeViewActor, UserManager}
import com.rbmhtechnology.eventuate.ReplicationEndpoint
import com.rbmhtechnology.eventuate.log.leveldb.LeveldbEventLog


@Singleton
class ActorSystemInitializer @Inject()(system: ActorSystem) {

  val endpoint = ReplicationEndpoint(id => LeveldbEventLog.props(id))(system)
  endpoint.activate()

  // Initialise event log
  val eventLog = endpoint.logs(ReplicationEndpoint.DefaultLogName)
  // Init User Manager
  val userManagerProps = Props(
    new UserManager(UserManager.ID, Some(UserManager.ID), eventLog)
  )
  system.actorOf(userManagerProps, UserManager.NAME)

  val cumulativeTradeViewProps = Props(
    new CumulativeTradeViewActor(CumulativeTradeViewActor.ID, Some(CumulativeTradeViewActor.ID), eventLog)
  )
  system.actorOf(cumulativeTradeViewProps, CumulativeTradeViewActor.NAME)

}
