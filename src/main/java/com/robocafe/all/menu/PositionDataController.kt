package com.robocafe.all.menu

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@Profile("positionDataService")
@RequestMapping("/positions")
class PositionDataController @Autowired constructor(
        private val positionRepository: PositionRepository
) {

    @GetMapping
    fun get(@RequestParam params: MultiValueMap<String, String>):
            ResponseEntity<Collection<PositionInfo>> {
        val ids = params["ids"]
        val categories = if(params.containsKey("categories"))
                params["categories"]!!.map { Category.valueOf(it) }.toHashSet()
                    else null
        return when {
            ids == null && categories == null ->
                ResponseEntity.ok(positionRepository.findAll().map { PositionInfo(it) })
            ids != null && categories == null ->
                ResponseEntity.ok(positionRepository.findAllById(ids).map { PositionInfo(it) })
            ids == null && categories != null ->
                ResponseEntity.ok(
                        positionRepository.findAllDistinctByCategoriesIn(categories).map { PositionInfo(it) }
                )
            ids != null && categories != null ->
                ResponseEntity.ok(
                        positionRepository.findAllDistinctByCategoriesInAndIdIn(
                                categories, ids
                        ).map { PositionInfo(it) }
                )
            else ->
                ResponseEntity.badRequest().build()
        }
    }
}