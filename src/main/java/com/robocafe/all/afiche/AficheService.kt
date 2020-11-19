package com.robocafe.all.afiche

import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlow
import java.net.URI

data class AficheInfo(
        val id: String,
        val title: String,
        val type: AficheType,
        val image: String?
)

data class AficheContent(
        val htmlConetent: String?,
        val url: String?
)

class WebAficheProvider(
        private val endpoint: URI
) {
    private val webClient = WebClient.builder()
                .baseUrl(endpoint.toString())
                .build()

    suspend fun fetchAfichePreview(): Set<AficheInfo> {
        return webClient.get().uri("/afiches")
                .retrieve()
                .bodyToFlow<AficheInfo>()
                .toCollection(mutableSetOf())
    }

    fun fetchAficheContent(id: String): AficheContent? {
        return webClient.get()
                .uri("/afiches/content")
                .attribute("id", id)
                .retrieve()
                .bodyToMono(AficheContent::class.java)
                .block()
    }
}

@Service
class AficheService @Autowired constructor(
        @Value("#{applicationConfiguration.afiches.endpoints}")
        endpoints: Collection<String>
) {
    private val providers = endpoints.map { WebAficheProvider(URI.create(it)) }

    fun getAfichePreviews(): Set<AficheInfo> {
        return runBlocking {
            providers.map { it.fetchAfichePreview() }
                    .flatten().toSet()
        }
    }

    fun getAficheContent(id: String): AficheContent {
        val result = providers.map { it.fetchAficheContent(id) }
                .firstOrNull()

        if (result == null) {
            throw AficheNotFound()
        }
        else {
            return result
        }
    }
}