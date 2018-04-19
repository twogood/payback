package se.activout.payback

import dagger.Component
import se.activout.kronslott.jdbi2.Jdbi2Module
import se.activout.payback.accounts.AccountResource
import se.activout.payback.oauth2.OAuth2Module
import se.activout.payback.oauth2.OAuth2Resource
import javax.inject.Singleton

@Singleton
@Component(modules = [BackendModule::class, OAuth2Module::class, Jdbi2Module::class])
interface BackendComponent {

    fun getOAuth2Resource(): OAuth2Resource
    fun getAccountResource(): AccountResource

}