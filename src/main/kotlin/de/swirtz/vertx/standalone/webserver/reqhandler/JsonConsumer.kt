package de.swirtz.vertx.standalone.webserver.reqhandler

import de.swirtz.vertx.standalone.webserver.incrementAndGetCounter
import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory

/**
 * Created on 05.05.2017.
 * @author: simon-wirtz
 */
class JsonConsumer : Handler<RoutingContext> {
    private companion object {
        val LOG = LoggerFactory.getLogger(SpecialHandler::class.java)
    }

    override fun handle(routingContext: RoutingContext) {
        val bodyAsString = routingContext.getBodyAsString("UTF-8")
        val req = routingContext.request()
        val reqNum = incrementAndGetCounter()
        LOG.debug("$reqNum. Got request from ${req.remoteAddress()}: method: ${req.method()}, path: ${req.path()}, request: $bodyAsString")
        val resp = "{\"jsonanswer\": \"anyresponse\"}"
        routingContext.response().setStatusCode(200).end(resp)
        LOG.debug("$reqNum. Ended Request with: $resp")
    }

}