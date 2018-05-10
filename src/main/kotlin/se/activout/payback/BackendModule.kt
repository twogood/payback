package se.activout.payback

import dagger.Module
import dagger.Provides
import io.dropwizard.setup.Environment

@Module
class BackendModule(private val configuration: BackendConfig, private val environment: Environment) {
    @Provides
    fun provideBackendConfig() = configuration

    @Provides
    fun provideEnvironment() = environment

    @Provides
    fun provideSebSettings() = configuration.seb.toSebSettings()

    @Provides
    fun provideSwedbankSettings() = configuration.swedbank.toSwedbankSettings()

    @Provides
    fun providenordeaSettings() = configuration.nordea.toNordeaSettings()
}