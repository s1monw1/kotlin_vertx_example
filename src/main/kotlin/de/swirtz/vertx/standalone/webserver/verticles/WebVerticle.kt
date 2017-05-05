package de.swirtz.vertx.standalone.webserver.verticles

import de.swirtz.vertx.standalone.webserver.JSON_CONT_TYPE
import de.swirtz.vertx.standalone.webserver.WEB_SRV_PORT
import de.swirtz.vertx.standalone.webserver.reqhandler.DefaultHandler
import de.swirtz.vertx.standalone.webserver.reqhandler.FailingHandler
import de.swirtz.vertx.standalone.webserver.reqhandler.JsonConsumer
import de.swirtz.vertx.standalone.webserver.reqhandler.SpecialHandler
import io.vertx.core.AbstractVerticle
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CookieHandler
import io.vertx.ext.web.handler.StaticHandler
import org.slf4j.LoggerFactory

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
        LOG.debug("WebVerticle start called; Going to register Router")
        val eventBus = vertx.eventBus()
        val router = Router.router(vertx)
        //These are optional but might be necessary in other routes
        router.route().handler(BodyHandler.create())
        router.route().handler(CookieHandler.create())
        router.route().failureHandler{
            LOG.error("ErrorHandler called! $it")
            it.response().setStatusCode(501).end("Sorry but I failed")
        }


        router.route(HttpMethod.GET, "/").produces(JSON_CONT_TYPE).handler(DefaultHandler(eventBus))
        router.route(HttpMethod.GET, "/special/*").produces(JSON_CONT_TYPE).handler(SpecialHandler())
        router.route(HttpMethod.POST, "/special/:quest").produces(JSON_CONT_TYPE).handler(SpecialHandler())
        router.route(HttpMethod.POST, "/jsonconsume").consumes(JSON_CONT_TYPE).produces(JSON_CONT_TYPE).handler(JsonConsumer())
        router.route(HttpMethod.GET, "/error/*").handler(FailingHandler())

        //Default:  static file dir is webroot
        router.route("/static/*").handler(StaticHandler.create())

        vertx.createHttpServer().requestHandler({ router.accept(it) }).listen(WEB_SRV_PORT)
    }

}