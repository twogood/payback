package se.activout.payback

import io.dropwizard.Application
import io.dropwizard.ConfiguredBundle
import io.dropwizard.auth.AuthDynamicFeature
import io.dropwizard.auth.AuthValueFactoryProvider
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import se.activout.kronslott.auth.session.SessionAuthBundle
import se.activout.kronslott.auth.session.SessionAuthFilter
import se.activout.kronslott.auth.session.SessionAuthenticator
import se.activout.kronslott.objectmapper.ObjectMapperBundle


class BackendApp : Application<BackendConfig>() {
    private lateinit var component: BackendComponent

    override fun initialize(bootstrap: Bootstrap<BackendConfig>) {
        bootstrap.addBundle(ObjectMapperBundle<BackendConfig>())
        bootstrap.addBundle(SessionAuthBundle<BackendConfig>())

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
                register(component.getOAuth2Resource())
                register(component.getAccountResource())

                register(AuthDynamicFeature(SessionAuthFilter.Builder<BackendUser>()
                        .setAuthenticator(SessionAuthenticator())
                        .buildAuthFilter()))
                register(AuthValueFactoryProvider.Binder(BackendUser::class.java))
            }

        }
    }
}