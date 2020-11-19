package com.robocafe.all.application.handlers.models

import com.robocafe.all.menu.CategoryHierarchyTree
import com.robocafe.all.menu.PositionInfo

data class FullMenuInfo(
        val categories: CategoryHierarchyTree,
        val positions: Set<PositionInfo>
)