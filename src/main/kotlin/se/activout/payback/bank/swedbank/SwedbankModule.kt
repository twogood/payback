package se.activout.payback.bank.swedbank

import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import se.activout.payback.domain.BankPlugin
import se.activout.payback.oauth2.domain.OAuth2Client
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class SwedbankInternal

@Module
object SwedbankModule {

    @Provides
    @IntoSet
    @JvmStatic
    fun provideBankPlugin(bank: Swedbank): BankPlugin = bank

    @SwedbankInternal
    @Provides
    @JvmStatic
    fun provideOAuth2Client(settings: SwedbankSettings): OAuth2Client {
        return SwedbankOAuth2Client(settings.accountApiUrl)
    }

}
