package io.defitrack.erc20

import io.codechef.defitrack.token.TokenService
import io.defitrack.common.network.Network
import io.defitrack.erc20.vo.ERC20Information
import io.defitrack.protocol.staking.Token
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.web3j.crypto.WalletUtils
import java.math.BigInteger

@RestController
class ERC20RestController(
    private val erC20Service: ERC20Service,
    private val tokenService: TokenService
) {

    @GetMapping("/{network}/{address}")
    fun getERC20Information(
        @PathVariable("network") network: Network,
        @PathVariable("address") address: String
    ): ResponseEntity<ERC20Information> {
        return erC20Service.getERC20Information(network, address)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @GetMapping("/{network}/{address}/token")
    fun getTokenInformation(
        @PathVariable("network") network: Network,
        @PathVariable("address") address: String
    ): ResponseEntity<Token> {
        if (!WalletUtils.isValidAddress(address)) {
            return ResponseEntity.badRequest().build()
        }

        return try {
            ResponseEntity.ok(
                tokenService.getTokenInformation(
                    address, network
                )
            )
        } catch (ex: Exception) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/{network}/{address}/{userAddress}")
    fun getBalance(
        @PathVariable("network") network: Network,
        @PathVariable("address") address: String,
        @PathVariable("userAddress") userAddress: String
    ): ResponseEntity<BigInteger> {

        if (!WalletUtils.isValidAddress(address)) {
            return ResponseEntity.badRequest().build()
        }
        if (!WalletUtils.isValidAddress(userAddress)) {
            return ResponseEntity.badRequest().build()
        }

        return try {
            ResponseEntity.ok(erC20Service.getBalance(network, address, userAddress))
        } catch (ex: Exception) {
            ex.printStackTrace()
            ResponseEntity.ok(BigInteger.ZERO)
        }
    }
}