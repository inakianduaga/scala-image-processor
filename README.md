Scala Image Processor demo
==========================

[![Heroku](http://heroku-badge.herokuapp.com/?app=scala-image-processor&style=flat)] 

> Small demo to test multi vs single thread performance when converting images

This small app applies a set of filters to an image (using [scrimage library](https://github.com/sksamuel/scrimage)) in order to test performance in two different execution contexts.

### [DEMO](http://scala-image-processor.herokuapp.com/)

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

Because of hosting limitations, images will be wiped 5 minutes after creation

