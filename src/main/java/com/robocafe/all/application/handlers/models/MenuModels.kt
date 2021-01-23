package com.robocafe.all.application.handlers.models

import com.robocafe.all.menu.PositionInfo

data class LocalizedCategoryHierarchyTreeNode(
        val category: String,
        val childes: Set<LocalizedCategoryHierarchyTreeNode>?
)

data class LocalizedCategoryHierarchyTree(
        val rootNodes: Set<LocalizedCategoryHierarchyTreeNode>
)

data class FullMenuInfo(
        val categories: LocalizedCategoryHierarchyTree,
        val positions: Set<PositionInfo>
)