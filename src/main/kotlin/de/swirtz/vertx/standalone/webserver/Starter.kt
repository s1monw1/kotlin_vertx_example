package de.swirtz.vertx.standalone.webserver

import de.swirtz.vertx.standalone.webserver.verticles.ServiceVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx

/**
 * Created on 07.04.2017.
 * @author: simon-wirtz
 */

fun main(args: Array<String>) {
    val vertx = Vertx.vertx()

    fun deploy(verticleFqcn: String, opt: DeploymentOptions = DeploymentOptions()): Unit {
        vertx.deployVerticle(verticleFqcn, opt, { deploy ->
            ServiceVerticle.LOG.info("$verticleFqcn has been deployed? ${deploy.succeeded()}")
            if (!deploy.succeeded()) {
                ServiceVerticle.LOG.error("$verticleFqcn deploy failed: ${deploy.cause()}", deploy.cause())
            }
        })
    }

    val opt = DeploymentOptions()
    opt.instances = 2
    deploy("de.swirtz.vertx.standalone.webserver.verticles.ServiceVerticle")
    deploy("de.swirtz.vertx.standalone.webserver.verticles.WebVerticle", opt)
}