package se.activout.payback.domain

import javax.inject.Inject

class AccountService @Inject constructor(private val banks: Set<@JvmSuppressWildcards BankPlugin>) {
    fun getAccounts(principal: BankPrincipal): List<BankAccount> {
        return banks.map { bank -> bank.accountBackend to principal.getCredential(bank.name) }
                .filter { (_, credential) -> credential != null }
                .map { (accountBackend, credential) -> accountBackend.getAccounts(credential!!) }
                .flatten()

    }
}