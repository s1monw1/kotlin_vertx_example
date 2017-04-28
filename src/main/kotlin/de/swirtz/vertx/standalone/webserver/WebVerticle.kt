package de.swirtz.vertx.standalone.webserver

import io.vertx.core.AbstractVerticle
import io.vertx.core.AsyncResult
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.eventbus.Message
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created on 07.04.2017.
 * @author: simon-wirtz
 */
class WebVerticle : AbstractVerticle() {
    init {
        LOG.debug("WebVerticle created")
    }
    companion object {
        val ACTION = "webservice.request.received"
        val LOG = LoggerFactory.getLogger(WebVerticle::class.java)
        val reqCount = AtomicInteger(0)
    }

    override fun start() {
        LOG.debug("WebVerticle start called")

        val httpServer = vertx.createHttpServer()
        val router = Router.router(vertx)
        router.route(HttpMethod.GET, "/").handler { routingContext ->
            val req = routingContext.request()
            val reqNum = reqCount.incrementAndGet()
            LOG.debug("$reqNum. Got request from ${req.remoteAddress()}: method: ${req.method()}, path: ${req.path()}")
            val serviceReq = JsonObject().put("request", "request json from WebVerticle").put("query", req.query())
            LOG.debug("send msg to eventBus:$ACTION: $serviceReq")
            val opt = DeliveryOptions()
            opt.sendTimeout = 30_000
            vertx.eventBus().send(ACTION, serviceReq, opt, { reply: AsyncResult<Message<Any>> ->
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
        httpServer.requestHandler({ router.accept(it) }).listen(8181, {LOG.debug("WebVerticle listening on 8181")})
    }
}