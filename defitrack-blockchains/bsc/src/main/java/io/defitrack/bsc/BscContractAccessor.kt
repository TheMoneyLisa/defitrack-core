package io.defitrack.bsc

import io.defitrack.abi.AbiDecoder
import io.defitrack.abi.domain.AbiContractEvent
import io.defitrack.common.network.Network
import io.defitrack.ethereumbased.contract.EvmContractAccessor
import io.reactivex.Flowable
import org.springframework.stereotype.Component
import org.web3j.abi.EventEncoder
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.datatypes.Event
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.Type
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.DefaultBlockParameterNumber
import org.web3j.protocol.core.methods.request.EthFilter
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.core.methods.response.EthCall
import org.web3j.protocol.http.HttpService
import org.web3j.protocol.websocket.WebSocketClient
import org.web3j.protocol.websocket.WebSocketService
import java.net.URI
import java.util.*

@Component
class BscContractAccessor(abiDecoder: AbiDecoder, val bscGateway: BscGateway) :
    EvmContractAccessor(abiDecoder) {



    fun listenToEvents(address: String, abi: String, event: String): Flowable<Map<String, Any>> {
        getEvent(abi, event)?.let { contractEvent ->
            val theEvent = Event(
                contractEvent.name,
                contractEvent.inputs
                    .map { fromDataTypes(it.type, it.indexed) }
                    .toList()
            )

            val ethFilter = EthFilter(
                DefaultBlockParameterName.LATEST,
                DefaultBlockParameterName.LATEST, address
            )
            ethFilter.addOptionalTopics(EventEncoder.encode(theEvent))
            return createEventListener(ethFilter, theEvent, contractEvent)
        } ?: return Flowable.empty()
    }

    private fun createEventListener(
        ethFilter: EthFilter,
        theEvent: Event,
        contractEvent: AbiContractEvent
    ): Flowable<Map<String, Any>> {
        return bscGateway.web3j().ethLogFlowable(ethFilter)
            .map { logs ->
                val indexedValues = ArrayList<Type<*>>()
                val nonIndexedValues = FunctionReturnDecoder.decode(
                    logs.data, theEvent.nonIndexedParameters
                )

                val indexedParameters = theEvent.indexedParameters
                for (i in indexedParameters.indices) {
                    val value = FunctionReturnDecoder.decodeIndexedValue(
                        logs.topics[i + 1], indexedParameters[i]
                    )
                    indexedValues.add(value)
                }

                val collect = indexedValues + nonIndexedValues

                contractEvent.inputs
                    .map { input -> input.name to collect[contractEvent.inputs.indexOf(input)].value }
                    .toMap()
            }
    }

    override fun getMulticallContract(): String {
       return  "0x41263cba59eb80dc200f3e2544eda4ed6a90e76c"
    }

    override fun getNetwork(): Network {
        return Network.BSC
    }

    override fun executeCall(from: String?, address: String, function: Function, endpoint: String?): List<Type<*>> {
        val encodedFunction = FunctionEncoder.encode(function)
        val ethCall = call(from, address, encodedFunction, endpoint)
        return FunctionReturnDecoder.decode(ethCall.value, function.outputParameters)
    }

    private fun call(
        from: String? = "0x0000000000000000000000000000000000000000",
        contract: String,
        encodedFunction: String,
        endpoint: String?
    ): EthCall {
        val web3j = endpoint?.let {
            constructEndpoint(it)
        } ?: bscGateway.web3j()

        return web3j.ethCall(
            Transaction.createEthCallTransaction(
                from,
                contract,
                encodedFunction
            ), DefaultBlockParameterName.LATEST
        ).send()
    }

    private fun constructEndpoint(endpoint: String): Web3j {
        return if (endpoint.startsWith("ws")) {
            val webSocketClient = WebSocketClient(URI.create(endpoint))
            val webSocketService = WebSocketService(webSocketClient, false)
            webSocketService.connect()
            Web3j.build(webSocketService)
        } else {
            Web3j.build(HttpService(endpoint, false))
        }
    }
}