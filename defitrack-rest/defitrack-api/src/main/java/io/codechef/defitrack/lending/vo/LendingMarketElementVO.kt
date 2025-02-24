package io.codechef.defitrack.lending.vo

import io.codechef.defitrack.network.NetworkVO
import io.codechef.defitrack.protocol.ProtocolVO
import java.math.BigDecimal

data class LendingMarketElementVO(
    val name: String,
    val protocol: ProtocolVO,
    val network: NetworkVO,
    val token: LendingMarketElementToken,
    val rate: Double,
    val poolType: String,
    val marketSize: BigDecimal = BigDecimal.ZERO
)

data class LendingMarketElementToken(
    val name: String,
    val symbol: String,
    val address: String,
    val logo: String
)