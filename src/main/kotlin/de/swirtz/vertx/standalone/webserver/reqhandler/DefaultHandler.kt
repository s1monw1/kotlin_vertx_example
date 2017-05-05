package de.swirtz.vertx.standalone.webserver.reqhandler

import de.swirtz.vertx.standalone.webserver.ACTION_WEB_REQ_RECEIVED
import de.swirtz.vertx.standalone.webserver.verticles.WebVerticleRequestCounter
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.EventBus
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory

/**
 * Created on 28.04.2017.
 * @author: simon-wirtz
 */

class DefaultHandler(val eventBus: EventBus) : Handler<RoutingContext> {
    private companion object {
        val LOG = LoggerFactory.getLogger(DefaultHandler::class.java)
    }

    override fun handle(routingContext: RoutingContext) {
        val req = routingContext.request()
        val reqNum = WebVerticleRequestCounter.incrementAndGetCounter()
        LOG.debug("$reqNum. Got request from ${req.remoteAddress()}: method: ${req.method()}, path: ${req.path()}")
        val serviceReq = JsonObject().put("request", "request json from WebVerticle").put("query", req.query())
        LOG.debug("send msg to eventBus:$ACTION_WEB_REQ_RECEIVED: $serviceReq")
        val opt = DeliveryOptions()
        opt.sendTimeout = 30_000
        eventBus.send(ACTION_WEB_REQ_RECEIVED, serviceReq, opt, { reply: AsyncResult<Message<Any>> ->
            val response = routingContext.response().putHeader("Content-Type", "application/json")
            if (reply.failed()) {
                LOG.error("$reqNum. FAILED! ${reply.cause()}")
                response.setStatusCode(500).end("{\"error\": \" ${reply.cause()}: error returned by ServiceVerticle!\"}")
            } else {
                val replyBody = reply.result()?.body() as? JsonObject
                LOG.debug("$reqNum. Got Reply from event consumer: $replyBody")
                response.setStatusCode(200).end(replyBody?.encodePrettily() ?: "{\"error\": \"null returned by ServiceVerticle!\"}")
            }
        })
    }

}