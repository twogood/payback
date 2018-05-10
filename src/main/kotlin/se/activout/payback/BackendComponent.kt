package se.activout.payback

import dagger.Component
import se.activout.payback.app.frontend.AccountResource
import se.activout.payback.app.frontend.BankResource
import se.activout.payback.bank.nordea.NordeaModule
import se.activout.payback.bank.seb.SebModule
import se.activout.payback.bank.swedbank.SwedbankModule
import se.activout.payback.oauth2.OAuth2Module
import se.activout.payback.oauth2.frontend.OAuth2Resource
import javax.inject.Singleton

@Singleton
@Component(modules = [BackendModule::class, OAuth2Module::class, NordeaModule::class, SebModule::class, SwedbankModule::class])
interface BackendComponent {

    fun getBankResource(): BankResource
    fun getOAuth2Resource(): OAuth2Resource
    fun getAccountResource(): AccountResource

}