import java.time.Clock

import com.google.inject.AbstractModule
import com.kkanojia.example.actors.{WSUserActor, WSUserParentActor}
import com.kkanojia.example.modules.ActorSystemInitializer

import play.api.libs.concurrent.AkkaGuiceSupport

import services.ApplicationTimer

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.
 *
 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
class Module extends AbstractModule with AkkaGuiceSupport {

  override def configure() = {
    bind(classOf[ActorSystemInitializer]).asEagerSingleton()

    bind(classOf[Clock]).toInstance(Clock.systemDefaultZone)
    bind(classOf[ApplicationTimer]).asEagerSingleton()

    bindActor[WSUserParentActor]("userParentActor")
    bindActorFactory[WSUserActor, WSUserActor.Factory]
  }

}
