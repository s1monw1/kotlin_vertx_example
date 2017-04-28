package de.swirtz.vertx.standalone

import io.vertx.core.AbstractVerticle
import io.vertx.core.AsyncResult
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created on 07.04.2017.
 * @author: simon-wirtz
 */
class WebVerticle : AbstractVerticle() {

    companion object {
        val ACTION = "ACTION"
        val LOG = LoggerFactory.getLogger(WebVerticle::class.java)
        val reqCount = AtomicInteger(0)
    }

    override fun start() {
        vertx.createHttpServer().requestHandler { req ->
            LOG.debug("Got request: ${req.host()}")
            val reqNum = reqCount.incrementAndGet();
            val serviceReq = JsonObject().put("request", "$reqNum. request json from WebVerticle").put("query", req.query())
            LOG.debug("send request: $serviceReq")
            vertx.eventBus().send(ACTION, serviceReq, { reply: AsyncResult<Message<Any>> ->
                if (reply.failed()) {
                    LOG.error("$reqNum. FAILED! ${reply.cause()}")
                    req.response().putHeader("Content-Type", "application/json").end("{\"error\": \" ${reply.cause()}: error returned by ServiceVerticle!\"}")
                } else {
                    val replyBody = reply.result()?.body() as? JsonObject
                    LOG.debug("$reqNum. Got Reply from event consumer: $replyBody")
                    req.response().putHeader("Content-Type", "application/json").end(replyBody?.encodePrettily() ?: "{\"error\": \"null returned by ServiceVerticle!\"}")
                }
            })
        }.listen(8181)
    }
}