package se.activout.payback.accounts.backend

import dagger.Binds
import dagger.Module
import dagger.Provides
import se.activout.payback.accounts.domain.AccountBackend
import se.activout.payback.oauth2.client.OAuth2ClientImpl
import se.activout.payback.oauth2.domain.OAuth2Client
import javax.inject.Qualifier


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AccountApi()

@Module
abstract class AccountBackendModule {

    @Binds
    abstract fun bindAccountBackend(accountBackendImpl: AccountBackendImpl): AccountBackend

    companion object {
        @AccountApi
        @Provides
        @JvmStatic
        fun provideOAuth2Client(settings: AccountApiSettings): OAuth2Client {
            return OAuth2ClientImpl(settings.url)
        }
    }
}