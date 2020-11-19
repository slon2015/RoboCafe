package com.robocafe.all.menu

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PositionRepository: JpaRepository<Position, String> {
    fun findAllDistinctByCategoriesIn(categories: Collection<Category>): Set<Position>
    fun findAllDistinctByCategoriesInAndIdIn(categories: Collection<Category>,
                                             ids: Collection<String>): Set<Position>
}