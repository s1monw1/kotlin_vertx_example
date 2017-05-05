package de.swirtz.vertx.standalone.webserver.verticles

import de.swirtz.vertx.standalone.webserver.WEB_SRV_PORT
import de.swirtz.vertx.standalone.webserver.reqhandler.DefaultHandler
import de.swirtz.vertx.standalone.webserver.reqhandler.SpecialHandler
import io.vertx.core.AbstractVerticle
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created on 07.04.2017.
 * @author: simon-wirtz
 */
class WebVerticle : AbstractVerticle() {

    companion object {
        private val LOG = LoggerFactory.getLogger(WebVerticle::class.java)
    }

    init {
        LOG.debug("WebVerticle created")
    }

    override fun start() {
        LOG.debug("WebVerticle start called")
        val eventBus = vertx.eventBus()
        val httpServer = vertx.createHttpServer()
        val router = Router.router(vertx)
        router.route(HttpMethod.GET, "/").handler(DefaultHandler(eventBus))
        router.route(HttpMethod.GET, "/special").handler(SpecialHandler())

        httpServer.requestHandler({ router.accept(it) }).listen(WEB_SRV_PORT)
    }

}