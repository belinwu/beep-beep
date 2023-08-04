package org.thechance.service_identity.domain.entity

data class Wallet(
    val id: String,
    val userId: String,
    val walletBalance: Double,
    val isDeleted: Boolean = false
)
