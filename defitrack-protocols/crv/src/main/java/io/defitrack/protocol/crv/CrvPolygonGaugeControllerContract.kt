package io.defitrack.protocol.crv

import io.defitrack.ethereumbased.contract.EvmContract
import io.defitrack.ethereumbased.contract.EvmContractAccessor
import io.defitrack.ethereumbased.contract.EvmContractAccessor.Companion.toAddress
import io.defitrack.ethereumbased.contract.EvmContractAccessor.Companion.toUint256
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Utf8String
import org.web3j.abi.datatypes.generated.Int128
import org.web3j.abi.datatypes.generated.Uint256
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

class CrvPolygonGaugeControllerContract(
    ethereumContractAccessor: EvmContractAccessor,
    abi: String,
    address: String,
) : EvmContract(
    ethereumContractAccessor,
    abi,
    address
) {

    fun numberOfGauges(): Int {
        return (read(
            "n_gauges",
            inputs = emptyList(),
            outputs = listOf(
                TypeReference.create(Int128::class.java)
            )
        )[0].value as BigInteger).toInt()
    }

    fun getGauge(gaugeNumber: Int): String {
        return read(
            "gauges",
            inputs = listOf(
                BigInteger.valueOf(gaugeNumber.toLong()).toUint256(),
            ),
            outputs = listOf(
                TypeReference.create(Address::class.java)
            )
        )[0].value as String
    }
}

class CrvPolygonGauge(
    ethereumContractAccessor: EvmContractAccessor,
    abi: String,
    address: String,
) : EvmContract(ethereumContractAccessor, abi, address) {

    fun balanceOf(address: String): BigInteger {
        return read(
            "balanceOf",
            inputs = listOf(address.toAddress()),
            outputs = listOf(
                TypeReference.create(Uint256::class.java)
            )
        )[0].value as BigInteger
    }

    val name: String by lazy {
        read(
            "name",
            inputs = emptyList(),
            outputs = listOf(
                TypeReference.create(Utf8String::class.java)
            )
        )[0].value as String
    }

    val symbol: String by lazy {
        read(
            "symbol",
            inputs = emptyList(),
            outputs = listOf(
                TypeReference.create(Utf8String::class.java)
            )
        )[0].value as String
    }

    val controller by lazy {
        read("controller", outputs = listOf(TypeReference.create(Address::class.java)))[0].value as String
    }

    val decimals: BigInteger by lazy {
        read(
            "decimals",
            inputs = emptyList(),
            outputs = listOf(
                TypeReference.create(Uint256::class.java)
            )
        )[0].value as BigInteger
    }

    fun decimalBalanceOf(address: String): BigDecimal {
        return balanceOf(address).toBigDecimal().divide(BigDecimal.TEN.pow(decimals.toInt()), 4, RoundingMode.HALF_UP)
    }

    val lpToken: String by lazy {
        read(
            "lp_token",
            inputs = emptyList(),
            outputs = listOf(
                TypeReference.create(Address::class.java)
            )
        )[0].value as String
    }
}

