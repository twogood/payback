package se.activout.payback

import io.dropwizard.Application
import io.dropwizard.ConfiguredBundle
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment

class BackendApp : Application<BackendConfig>() {
    private lateinit var component: BackendComponent

    override fun initialize(bootstrap: Bootstrap<BackendConfig>) {

        bootstrap.addBundle(object : ConfiguredBundle<BackendConfig> {
            override fun run(configuration: BackendConfig, environment: Environment) {
                component = DaggerBackendComponent.builder()
                        .backendModule(BackendModule(configuration, environment))
                        .build()
            }

            override fun initialize(bootstrap: Bootstrap<*>) {}
        })
    }

    override fun run(configuration: BackendConfig, environment: Environment) {
        environment.apply {

            jersey().apply {
                register(component.getAuthResource())
            }

        }
    }
}