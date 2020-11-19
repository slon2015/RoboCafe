package com.robocafe.all.menu

import javax.persistence.*

enum class Category(val parent: Category? = null) {
    FOOD,
    DRINKS,
    COMBO_MEALS(FOOD),
    TEA(DRINKS),
    JUICE(DRINKS)
}

@Entity
@Table(name = "menu_position")
// TODO refactor to aggregate
class Position(@field:Id val id: String, var name: String,
               @field:Enumerated(EnumType.STRING)
               @field:ElementCollection
               @field:CollectionTable(name = "menu_position_categories")
               val categories: MutableSet<Category>,
               var price: Double, var image: String?)