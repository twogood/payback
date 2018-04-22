package se.activout.payback

import dagger.Component
import se.activout.kronslott.jdbi2.Jdbi2Module
import se.activout.payback.accounts.backend.AccountBackendModule
import se.activout.payback.accounts.frontend.AccountResource
import se.activout.payback.oauth2.OAuth2Module
import se.activout.payback.oauth2.frontend.OAuth2Resource
import javax.inject.Singleton

@Singleton
@Component(modules = [BackendModule::class, OAuth2Module::class, AccountBackendModule::class, Jdbi2Module::class])
interface BackendComponent {

    fun getOAuth2Resource(): OAuth2Resource
    fun getAccountResource(): AccountResource

}