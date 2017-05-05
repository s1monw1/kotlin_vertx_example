package de.swirtz.vertx.standalone.webserver.verticles

import java.util.concurrent.atomic.AtomicInteger

/**
 * Created on 05.05.2017.
 * @author: simon-wirtz
 *
 * Singleton to be used by all RequestHandlers for logging purposes
 *
 */
object WebVerticleRequestCounter{
    private val counter: AtomicInteger = AtomicInteger(0)

    fun incrementAndGetCounter() = counter.incrementAndGet()
}