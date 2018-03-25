package se.activout.payback

import dagger.Component
import se.activout.payback.auth.AuthModule
import se.activout.payback.auth.AuthResource
import javax.inject.Singleton

@Singleton
@Component(modules = [BackendModule::class, AuthModule::class])
interface BackendComponent {

    fun getAuthResource(): AuthResource
}