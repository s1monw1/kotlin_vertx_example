package de.swirtz.vertx.standalone.webserver

import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import org.slf4j.LoggerFactory

/**
 * Created on 07.04.2017.
 * @author: simon-wirtz
 */

fun main(args: Array<String>) {
    val LOG = LoggerFactory.getLogger("My-Vertx-App")
    val vertx = Vertx.vertx()

    fun deploy(verticleClassName: String, opt: DeploymentOptions = DeploymentOptions()) {
        vertx.deployVerticle(verticleClassName, opt, { deploy ->
            LOG.info("$verticleClassName has been deployed? ${deploy.succeeded()}")
            if (!deploy.succeeded()) {
                LOG.error("$verticleClassName deploy failed: ${deploy.cause()}", deploy.cause())
            }
        })
    }

    val opt = DeploymentOptions()
    opt.instances = 2
    deploy("de.swirtz.vertx.standalone.webserver.verticles.ServiceVerticle")
    deploy("de.swirtz.vertx.standalone.webserver.verticles.WebVerticle", opt)
}