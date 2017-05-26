package de.swirtz.vertx.standalone.webserver.reqhandler

import de.swirtz.vertx.standalone.webserver.incrementAndGetCounter
import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory

/**
 * Created on 28.04.2017.
 * @author: simon-wirtz
 */

class SpecialHandler : Handler<RoutingContext> {
    private companion object {
        val LOG = LoggerFactory.getLogger(SpecialHandler::class.java)
    }

    override fun handle(routingContext: RoutingContext) {

        val req = routingContext.request()
        val reqNum = incrementAndGetCounter()
        val questParam = routingContext.request().getParam("quest")
        LOG.debug("$reqNum. Got request from ${req.remoteAddress()}: method: ${req.method()}, path: ${req.path()}, quest=$questParam, acceptablecontent: ${routingContext.acceptableContentType}")
        val resp = "{\"specialcontent\": \"hello world: ${questParam ?: "noParam"}\"}"
        routingContext.response().setStatusCode(200).end(resp)
        LOG.debug("$reqNum. Ended Request with: $resp")
    }

}