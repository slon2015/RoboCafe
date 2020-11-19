package com.robocafe.all.afiche

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@Profile("aficheDataService")
@RequestMapping("/afiches")
class AficheDataController @Autowired constructor(
        private val aficheRepository: AficheRepository
) {

    private fun mapIntoInfo(data: Afiche) =
            AficheInfo(
                data.id,
                data.title,
                data.type,
                data.image
            )

    private fun mapIntoContent(data: Afiche) =
            AficheContent(
                    data.htmlContent,
                    data.url
            )

    @GetMapping
    fun getInfo(): Set<AficheInfo> {
        return aficheRepository.findAll().map { mapIntoInfo(it) }.toSet()
    }

    @GetMapping("/content")
    fun getContent(@RequestParam id: String): ResponseEntity<AficheContent> {
        val result = aficheRepository.findById(id).map { mapIntoContent(it) }
        return if (result.isPresent) {
            ResponseEntity.ok(result.get())
        }
        else {
            ResponseEntity.notFound().build()
        }
    }
}