[![Build Status](https://travis-ci.org/kunalkanojia/react-play-eventsourcing.svg?branch=master)](https://travis-ci.org/kunalkanojia/react-play-eventsourcing)

### Scala event sourcing example.

The example application uses Play 2.5 and Eventuate(https://github.com/RBMHTechnology/eventuate) for event sourcing.

The events are written to a LevelDB log.

The application also implements a sample websocket to push all the trade messages to connected clients in json.

The app is deployed on heroku, you can access it here - [http://play-eventsourcing.herokuapp.com/](http://play-eventsourcing.herokuapp.com/)


Find more information read this blog post - [https://kunalkanojia.github.io/blog/event-sourcing-part-1](https://kunalkanojia.github.io/blog/event-sourcing-part-1)
