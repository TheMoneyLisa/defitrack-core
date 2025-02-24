package io.codechef.defitrack.protocol.quickswap.staking

import io.codechef.defitrack.staking.StakingMarketService
import io.codechef.defitrack.staking.domain.RewardToken
import io.codechef.defitrack.staking.domain.StakedToken
import io.codechef.defitrack.staking.domain.StakingMarketElement
import io.defitrack.abi.ABIResource
import io.defitrack.common.network.Network
import io.defitrack.polygon.config.PolygonContractAccessor
import io.defitrack.protocol.Protocol
import io.defitrack.protocol.staking.TokenType
import io.defitrack.quickswap.QuickswapService
import io.defitrack.quickswap.contract.DQuickContract
import org.springframework.stereotype.Service

@Service
class DQuickStakingMarketService(
    private val quickswapService: QuickswapService,
    private val polygonContractAccessor: PolygonContractAccessor,
    private val abiResource: ABIResource,
) : StakingMarketService {

    val dquickStakingABI by lazy {
        abiResource.getABI("quickswap/dquick.json")
    }

    val dquick = dquickContract()

    override fun getStakingMarkets(): List<StakingMarketElement> {
        return listOf(
            StakingMarketElement(
                id = "polygon-dquick-${dquick.address.lowercase()}",
                network = getNetwork(),
                protocol = getProtocol(),
                name = "DQuick",
                token = StakedToken(
                    name = "Quick",
                    symbol = "QUICK",
                    address = "0x831753dd7087cac61ab5644b308642cc1c33dc13",
                    network = getNetwork(),
                    decimals = 18,
                    type = TokenType.SINGLE
                ),
                rewardToken = RewardToken(
                    name = "Quick",
                    symbol = "QUICK",
                    decimals = 18,
                ),
                contractAddress = dquick.address,
                vaultType = "quickswap-dquick",
                marketSize = 0.0,
                rate = 0.0
            )
        )
    }

    private fun dquickContract() = DQuickContract(
        polygonContractAccessor,
        dquickStakingABI,
        quickswapService.getDQuickContract(),
    )

    override fun getProtocol(): Protocol {
        return Protocol.QUICKSWAP
    }

    override fun getNetwork(): Network {
        return Network.POLYGON
    }
}