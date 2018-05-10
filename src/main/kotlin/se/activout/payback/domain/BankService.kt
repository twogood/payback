package se.activout.payback.domain

import javax.inject.Inject

class BankService @Inject constructor(bankSet: Set<@JvmSuppressWildcards BankPlugin>) {

    private val banks: Map<BankName, BankPlugin> = bankSet.associateBy { it.name }

    fun getBankNames(): Iterable<BankName> {
        return banks.keys
    }

    fun getBank(bankName: BankName): BankPlugin? {
        return banks[bankName]
    }
}