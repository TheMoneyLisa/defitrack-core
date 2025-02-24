package io.codechef

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class PolycatApplication

fun main(args: Array<String>) {
    runApplication<PolycatApplication>(*args)
}