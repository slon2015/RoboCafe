package com.robocafe.all.application.handlers

import com.robocafe.all.application.handlers.models.FullMenuInfo
import com.robocafe.all.application.handlers.models.LocalizedCategoryHierarchyTree
import com.robocafe.all.application.handlers.models.LocalizedCategoryHierarchyTreeNode
import com.robocafe.all.menu.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/menu")
class MenuController @Autowired constructor(
        private val positionService: PositionService
) {

    private fun localizeNode(node: CategoryHierarchyTreeNode,
                             localization: String?): LocalizedCategoryHierarchyTreeNode {
        return LocalizedCategoryHierarchyTreeNode(
                if ("ru" == localization && node.category.ru != null)
                    node.category.ru else node.category.toString(),
                node.childes?.map { localizeNode(it, localization) }?.toSet()
        )
    }

    private fun localizeCategoryHierarchy(
            nativeHierarchy: CategoryHierarchyTree,
            localization: String?
    ): LocalizedCategoryHierarchyTree {
        return LocalizedCategoryHierarchyTree(
                nativeHierarchy.rootNodes.map {
                    localizeNode(it, localization)
                }.toSet()
        )
    }

    @GetMapping
    fun fullMenu(@RequestParam localisation: String?): ResponseEntity<FullMenuInfo> {
        return ResponseEntity.ok(
                FullMenuInfo(
                    localizeCategoryHierarchy(
                            positionService.getCategoryHierarchy(),
                            localisation
                    ),
                    positionService.getAllPositions()
                )
        )
    }

    @GetMapping("/for/{category}")
    fun menuForCategory(@PathVariable category: Category): ResponseEntity<Set<PositionInfo>> {
        return ResponseEntity.ok(positionService.getPositionsForCategory(category))
    }

    @GetMapping("/{id}")
    fun positionForId(@PathVariable id: String):ResponseEntity<PositionInfo> {
        return ResponseEntity.ok(positionService.getPositionInfo(id))
    }
}