package io.codechef.defitrack.protocol.compound.config

import io.codechef.defitrack.config.DefaultCacheConfig
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
class CacheConfig : DefaultCacheConfig() {

    override fun caches(): List<CaffeineCache> {
        return listOf(
            buildCache("compound-mainnet-pools", 10, TimeUnit.DAYS)
        )
    }
}