package de.swirtz.vertx.standalone.webserver.reqhandler

import de.swirtz.vertx.standalone.webserver.JSON_CONT_TYPE
import de.swirtz.vertx.standalone.webserver.verticles.WebVerticleRequestCounter
import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory

/**
 * Created on 28.04.2017.
 * @author: simon-wirtz
 */

class FailingHandler : Handler<RoutingContext> {
    private companion object {
        val LOG = LoggerFactory.getLogger(SpecialHandler::class.java)
    }

    override fun handle(routingContext: RoutingContext) {
        throw IllegalArgumentException("I am the exception")
    }

}