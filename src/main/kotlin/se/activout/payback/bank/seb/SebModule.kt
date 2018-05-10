package se.activout.payback.bank.seb

import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import se.activout.payback.domain.BankPlugin
import se.activout.payback.oauth2.client.OAuth2ClientImpl
import se.activout.payback.oauth2.domain.OAuth2Client
import javax.inject.Qualifier


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class SebInternal()

@Module
object SebModule {


//    @Binds
//    abstract fun bindAccountBackend(accountBackendImpl: AccountBackendImpl): AccountBackend

//    companion object {
        @Provides
        @IntoSet
        @JvmStatic
        fun provideBank(seb: Seb): BankPlugin = seb

        @SebInternal
        @Provides
        @JvmStatic
        fun provideOAuth2Client(settings: SebSettings): OAuth2Client {
            return OAuth2ClientImpl(settings.accountApiUrl)
        }
 //   }
}