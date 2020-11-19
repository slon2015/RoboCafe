package com.robocafe.all.menu

import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlow
import java.net.URI

data class PositionInfo(val id: String,
                        val name: String,
                        val categories: Set<Category>,
                        val price: Double,
                        val image: String?
) {
    constructor(data: Position):
            this(data.id, data.name, data.categories, data.price, data.image)
}

interface PositionProvider {
    suspend fun getAllPositions(): Set<PositionInfo>
    suspend fun getPositions(ids: Set<String>): Set<PositionInfo>
    suspend fun getPositionsByCategory(categories: Set<Category>): Set<PositionInfo>
}

class WebPositionProvider(
        endpoint: URI
): PositionProvider {

    private val webClient = WebClient.builder()
                .baseUrl(endpoint.toString())
                .build()

    override suspend fun getAllPositions(): Set<PositionInfo> {
        return webClient.get().uri("/positions")
                .retrieve().bodyToFlow<PositionInfo>()
                .toCollection(mutableSetOf())
    }

    override suspend fun getPositions(ids: Set<String>): Set<PositionInfo> {
        return webClient.get().uri("/positions")
                .attribute("ids", ids)
                .retrieve().bodyToFlow<PositionInfo>()
                .toCollection(mutableSetOf())
    }

    override suspend fun getPositionsByCategory(categories: Set<Category>): Set<PositionInfo> {
        return webClient.get().uri("/positions")
                .attribute("categories", categories.map { it.toString() }.toSet())
                .retrieve().bodyToFlow<PositionInfo>()
                .toCollection(mutableSetOf())
    }
}

data class CategoryHierarchyTreeNode(
        val category: Category,
        val childes: Set<CategoryHierarchyTreeNode>?
)

data class CategoryHierarchyTree(
        val rootNodes: Set<CategoryHierarchyTreeNode>
)

fun findNodeFor(tree: CategoryHierarchyTree, category: Category): CategoryHierarchyTreeNode? {
    fun checkNode(node: CategoryHierarchyTreeNode): CategoryHierarchyTreeNode? {
        when {
            node.category == category ->
                return node
            node.childes == null ->
                return null
            else -> {
                for (child in node.childes) {
                    val current = checkNode(child)
                    if (current != null) {
                        return current
                    }
                }
                return null
            }
        }
    }
    for (node in tree.rootNodes) {
        val branchResult = checkNode(node)
        if (branchResult != null) {
            return branchResult
        }
    }
    return null
}

@Service
class PositionService @Autowired constructor(
        @Value("#{applicationConfiguration.positions.endpoints}")
        endpoints: Set<String>?
) {
    private val providers: Set<PositionProvider>
            = endpoints?.map { WebPositionProvider(URI.create(it)) }?.toSet() ?: setOf()

    fun getAllPositions(): Set<PositionInfo> {
        return runBlocking {
            providers.map { it.getAllPositions() }
                    .flatten().toSet()
        }
    }

    fun getPositionsForCategory(category: Category): Set<PositionInfo> {
        val positionInHierarchy = findNodeFor(getCategoryHierarchy(), category)!!
        val categoriesForSearch = if (positionInHierarchy.childes != null)
                positionInHierarchy.childes.map { it.category }.toMutableSet()
            else
                mutableSetOf()
        categoriesForSearch.add(category)
        return runBlocking {
            providers.map { it.getPositionsByCategory(categoriesForSearch) }
                    .flatten().toSet()
        }
    }

    fun getPositionForSpecifiedCategory(category: Category): Set<PositionInfo> {
        return runBlocking {
            providers.map { it.getPositionsByCategory(setOf(category)) }
                    .flatten().toSet()
        }
    }

    fun getPositionInfo(id: String): PositionInfo {
        val result = runBlocking {
            providers.map { it.getPositions(setOf(id)) }
                    .flatten().firstOrNull()
        }
        if (result == null) {
            throw PositionNotFound()
        }
        else {
            return result
        }
    }

    fun getCategoryHierarchy(): CategoryHierarchyTree {
        val rootCategories = Category.values().filter { it.parent == null }.toSet()
        fun createNode(category: Category): CategoryHierarchyTreeNode {
            val childes = Category.values().filter { it.parent == category }
                    .map { createNode(it) }.toSet()
            return CategoryHierarchyTreeNode(category,
                    if (childes.isEmpty()) null else childes)
        }
        return CategoryHierarchyTree(rootCategories.map { createNode(it) }.toSet())
    }
}