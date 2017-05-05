package de.swirtz.vertx.standalone.webserver

import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Created on 05.05.2017.
 * @author: simon-wirtz
 */

@RunWith(VertxUnitRunner::class)
class VertXTest {

    @Test
    fun vertxSuite(context: TestContext) {
        println("Test running")
    }
}

