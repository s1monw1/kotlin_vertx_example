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
import io.vertx.ext.web.handler.ResponseContentTypeHandler
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.ext.web.handler.TemplateHandler
import io.vertx.ext.web.templ.ThymeleafTemplateEngine
import org.slf4j.LoggerFactory

/**
 * Created on 07.04.2017.
 * @author: simon-wirtz
 *
 * Verticle to start a Webserver with different routes
 */
class WebVerticle(val port: Int = WEB_SRV_PORT) : AbstractVerticle() {

    companion object {
        private val LOG = LoggerFactory.getLogger(WebVerticle::class.java)
    }

    init {
        LOG.debug("WebVerticle created")
    }

    override fun start() {
        LOG.debug("WebVerticle start called; Going to register Router")
        val eventBus = vertx.eventBus()

        val router =Router.router(vertx).apply {
            //These are optional but might be necessary in other routes
            route().handler(BodyHandler.create())
            //router.route().handler(CookieHandler.create())
            route().failureHandler {
                it.response().setStatusCode(501).end("Sorry but I failed")
            }
            //Adds Content-Type Header
            route().handler(ResponseContentTypeHandler.create())

            route(HttpMethod.GET, "/").produces(JSON_CONT_TYPE).handler(DefaultHandler(eventBus))
            route(HttpMethod.GET, "/special/*").produces(JSON_CONT_TYPE).handler(SpecialHandler())
            route(HttpMethod.POST, "/special/:quest").produces(JSON_CONT_TYPE).handler(SpecialHandler())
            route(HttpMethod.POST, "/jsonconsume").consumes(JSON_CONT_TYPE).produces(JSON_CONT_TYPE).handler(JsonConsumer())
            route(HttpMethod.GET, "/error/*").handler(FailingHandler())
            //Default: static file dir is 'webroot'
            route("/static/*").handler(StaticHandler.create())
            //Default: dynamic file dir is 'templates'
            route("/dynamic/*").handler { context ->
                context.put("foo", "fooValue was added by different handler!")
                context.next()
            }
            route("/dynamic/*").handler(TemplateHandler.create(ThymeleafTemplateEngine.create()))


        }
        vertx.createHttpServer().requestHandler {
            router.accept(it)
        }.listen(port, {
            LOG.info("WebServer listening on $port")
        })
    }


}