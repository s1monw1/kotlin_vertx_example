package de.swirtz.vertx.standalone

import io.vertx.core.AbstractVerticle
import io.vertx.core.AsyncResult
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory

/**
 * Created on 07.04.2017.
 * @author: simon-wirtz
 */
class WebVerticle : AbstractVerticle() {

    companion object {
        val ACTION = "ACTION"
        val LOG = LoggerFactory.getLogger(WebVerticle::class.java)
    }

    override fun start() {
        vertx.createHttpServer().requestHandler { req ->
            LOG.debug("Got request: ${req.host()}")
            val serviceReq = JsonObject().put("request", "request json from WebVerticle").put("query", req.query())
            LOG.debug("send request: $serviceReq")
            vertx.eventBus().send(ACTION, serviceReq, { reply: AsyncResult<Message<Any>> ->
                val replyBody = reply.result()?.body() as JsonObject
                LOG.debug("Got Reply from event consumer: $replyBody")
                req.response().end(replyBody.encodePrettily())
            })
        }.listen(8181)
    }
}