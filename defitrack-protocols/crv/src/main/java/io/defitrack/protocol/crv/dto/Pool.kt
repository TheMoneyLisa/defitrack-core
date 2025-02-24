package io.defitrack.crv.dto

import java.math.BigDecimal
import java.math.BigInteger

class Pool(
    val id: String,
    val name: String,
    val swapAddress: String,
    val virtualPrice: String,
    val lpToken: LpToken,
    val underlyingCount: BigInteger,
    val coins: List<Coin>
)

class LpToken(
    val id: String,
    val address: String,
    val decimals: Int,
    val name: String,
    val symbol: String
)

class Coin(
    val id: String,
    val index: Int,
    val token: Token,
    val underlying: UnderlyingCoin
)

class UnderlyingCoin(
    val id: String,
    val index: Int,
    val balance: BigDecimal,
    val token: Token
)

class Token(
    val address: String,
    val decimals: Int,
    val name: String,
    val symbol: String,
)