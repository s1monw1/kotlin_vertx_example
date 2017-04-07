package de.swirtz.vertx.standalone

import io.vertx.core.Vertx

/**
 * Created on 07.04.2017.
 * @author: simon-wirtz
 */

fun main(args: Array<String>) {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(ServiceVerticle())
    vertx.deployVerticle(WebVerticle())
}