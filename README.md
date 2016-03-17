Scala Image Processor demo
==========================

![Heroku](http://heroku-badge.herokuapp.com/?app=scala-image-processor&style=flat) 

> Small demo to test multi vs single thread performance when converting images, using a combination of http / websockets for communicating between the client and server.

This small app applies a set of filters to an image (using [scrimage library](https://github.com/sksamuel/scrimage)) in order to test performance in two different execution contexts. 
Some of the tech used:

- Play framework for the MVC backend 
- Akka actor system to handle websocket communications
- BaconJS for handling websockets communications as streams

## [DEMO](http://scala-image-processor.herokuapp.com/)

## Execution Contexts:

Two different thread execution contexts are tested ([configuration](./conf/application.conf))

**Multi-Threaded**: Uses the default scala `fork-join-executor`  

```
multi-thread-context {
  type = Dispatcher
  executor = "fork-join-executor"
  fork-join-executor {
  }
  throughput = 100
}
```

**Single-Threaded**: Uses a `thread-pool-executor` restricted to 1 core.  

```
single-thread-context {
  type = Dispatcher
  executor = "thread-pool-executor"
  thread-pool-executor {
    fixed-pool-size = 1,
    # minimum number of threads to cap factor-based core number to
    core-pool-size-min = 1
    # No of core threads ... ceil(available processors * factor)
    core-pool-size-factor = 1.0
    # maximum number of threads to cap factor-based number to
    core-pool-size-max = 1
  },
  throughput = 1
}
```


## Generated Images:

Because of hosting limitations, *images will be wiped 5 minutes after creation*

## Websockets

Each pageview registers a websocket connection with the backend, and the websocket identifier is linked to the user session Id. When a request for processing images comes from the user,
the session ID is used to lookup the correct websocket to send the update after processing has finished. 

- Due to Heroku's hosting limitations, the client must send a ping to keep the connection alive every 30 secs.


