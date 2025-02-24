package io.defitrack.protocol.compound

import io.defitrack.ethereumbased.contract.EvmContract
import io.defitrack.ethereumbased.contract.EvmContractAccessor
import io.defitrack.ethereumbased.contract.EvmContractAccessor.Companion.toAddress
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.generated.Uint256
import java.math.BigDecimal
import java.math.BigInteger

class CompoundTokenContract(
    ethereumContractAccessor: EvmContractAccessor,
    abi: String,
    address: String,
) : EvmContract(
    ethereumContractAccessor, abi, address
) {

    val symbol: String by lazy {
        read(
            "symbol"
        )[0].value as String
    }


    val name: String by lazy {
        read(
            "name"
        )[0].value as String
    }


    val underlyingAddress: String? by lazy {
        try {

            read(
                "underlying"
            )[0].value as String
        } catch (ex: Exception) {
            null
        }
    }

    val decimals: BigInteger by lazy {
        try {
            read(
                "decimals"
            )[0].value as BigInteger
        } catch (ex: Exception) {
            BigInteger.valueOf(18)
        }
    }

    fun balanceOf(address: String): BigInteger {
        return read(
            "balanceOf",
            inputs = listOf(address.toAddress()),
            outputs = listOf(
                TypeReference.create(Uint256::class.java)
            )
        )[0].value as BigInteger
    }

    fun underlyingBalanceOf(address: String): BigDecimal {
        return balanceOf(address).times(exchangeRate).toBigDecimal()
            .divide(BigDecimal.TEN.pow(18))
    }

    val exchangeRate by lazy {
        read(
            "exchangeRateStored",
            outputs = listOf(
                TypeReference.create(Uint256::class.java)
            )
        )[0].value as BigInteger
    }

    fun borrowBalanceStored(address: String): BigInteger {
        return read(
            "borrowBalanceStored",
            inputs = listOf(address.toAddress()),
            outputs = listOf(TypeReference.create(Uint256::class.java))
        )[0].value as BigInteger
    }

    val supplyRatePerBlock by lazy {
        read(
            "supplyRatePerBlock",
            outputs = listOf(TypeReference.create(Uint256::class.java))
        )[0].value as BigInteger
    }

    val borrowRatePerBlock by lazy {
        read(
            "borrowRatePerBlock",
            outputs = listOf(TypeReference.create(Uint256::class.java))
        )[0].value as BigInteger
    }
}