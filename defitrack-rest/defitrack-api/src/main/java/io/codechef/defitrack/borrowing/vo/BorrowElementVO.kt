package io.codechef.defitrack.borrowing.vo

import io.codechef.defitrack.network.NetworkVO
import io.codechef.defitrack.protocol.ProtocolVO

data class BorrowElementVO(
    val user: String,
    val network: NetworkVO,
    val protocol: ProtocolVO,
    val dollarValue: Double,
    val name: String,
    val rate: Double?,
    val amount: String,
    val symbol: String,
    val tokenUrl: String
)