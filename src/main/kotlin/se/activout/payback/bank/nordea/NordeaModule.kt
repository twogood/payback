package se.activout.payback.bank.nordea

import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import se.activout.payback.domain.BankPlugin

@Module
object NordeaModule {
    @Provides
    @IntoSet
    @JvmStatic
    fun provideBank(bank: Nordea): BankPlugin = bank

}