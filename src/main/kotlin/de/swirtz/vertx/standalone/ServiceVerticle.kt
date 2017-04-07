package de.swirtz.vertx.standalone

import io.vertx.core.AbstractVerticle
import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory

/**
 * Created on 07.04.2017.
 * @author: simon-wirtz
 */

class ServiceVerticle : AbstractVerticle(){
    companion object {
        val LOG = LoggerFactory.getLogger(ServiceVerticle::class.java)
    }

    override fun start() {
        vertx.eventBus().consumer<JsonObject>(WebVerticle.ACTION, {msg->
            LOG.debug("Message ${msg.body()} received!")
            val serviceResp = msg.body()
            serviceResp.put("serviceVerticleResp","answering ACTION event")
            LOG.debug("Reply Message $serviceResp")
            msg.reply(serviceResp)
        })
    }
}