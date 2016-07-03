[![Build Status](https://travis-ci.org/kunalkanojia/react-play-eventsourcing.svg?branch=master)](https://travis-ci.org/kunalkanojia/react-play-eventsourcing)

Scala event sourcing example.

The example application uses Play 2.5 and Eventuate(https://github.com/RBMHTechnology/eventuate) for event sourcing.

The events are written to a LevelDB log.

The application also implements a sample websocket to push all the trade messages to connected clients in json.

Find more information here - https://kunalkanojia.github.io/play/framework/eventuate/scala/akka/2016/06/28/event-sourcing-part-1.html
