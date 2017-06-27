package de.swirtz.vertx.standalone.webserver

import de.swirtz.vertx.standalone.webserver.verticles.WebVerticle
import io.vertx.core.Vertx
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.RunTestOnContext
import io.vertx.ext.unit.junit.Timeout
import io.vertx.ext.unit.junit.VertxUnitRunner
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory

/**
 * Created on 05.05.2017.
 * @author: simon-wirtz
 */

@RunWith(VertxUnitRunner::class)
class WebVerticleTest {

    val webVertPort = 9988
    val LOG = LoggerFactory.getLogger(WebVerticleTest::class.java)

    @get:Rule
    var rule = RunTestOnContext()

    @get:Rule
    var timeout = Timeout.seconds(5)

    lateinit var vertx: Vertx

    @Before
    fun setup(context: TestContext) {
        vertx = rule.vertx()
        val webVerticle = WebVerticle(webVertPort)
        vertx.deployVerticle(webVerticle, context.asyncAssertSuccess { e->

            LOG.debug("WebVerticle accessible: ${webVerticle.deploymentID()}")
        })
    }

    @Test
    fun verifyWebVerticleIsDeployed(context: TestContext) {
        // Set the default host
        val client = vertx.createHttpClient()
        val async = context.async()
        client.getNow(webVertPort, "localhost", "/special", { response ->
            LOG.debug("Received response with status code ${response.statusCode()}")
            context.assertEquals(200, response.statusCode())
            response.bodyHandler { body ->
                val resp = body.getString(0, body.length())
                LOG.debug("Response: $resp")
                context.assertNotNull(body.toJsonObject().getString("specialcontent"))
            }
            //response.headers().forEach{LOG.info("found header $it")}
            //context.assertEquals(JSON_CONT_TYPE, response.headers().filter { it.key == "Content-Type" }.map { it.value })
            async.complete()
        })
    }

    @Test
    fun exampleTest(context: TestContext) {
        println("start")

        val async = context.async()

        vertx.eventBus().consumer<Any>("the-address", { msg ->
            async.complete()
            context.assertNotNull(msg)
        })
        println("sleep")
        Thread.sleep(2000)
        println("after sleep")
        vertx.eventBus().publish("the-address", "MyMessage")
        println("published")

    }

}

