package se.activout.payback.app.domain

import se.activout.payback.domain.BankCredential
import se.activout.payback.domain.BankName
import se.activout.payback.domain.BankPrincipal
import java.security.Principal

class BackendUser() : Principal, BankPrincipal {
    private val bankCredentials: HashMap<BankName, BankCredential> = hashMapOf()

    fun setCredential(bankName: BankName, bankCredential: BankCredential) {
        bankCredentials[bankName] = bankCredential
    }

    override fun getCredential(bankName: BankName): BankCredential? = bankCredentials[bankName]

    override fun getName(): String = "user"
}