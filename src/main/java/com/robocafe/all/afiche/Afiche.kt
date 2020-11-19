package com.robocafe.all.afiche

import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id

enum class AficheType {
    URL,
    HTML
}

@Entity
// TODO refactor to aggregate
class Afiche(
        @field:Id
        val id: String,
        @field:Enumerated(EnumType.STRING)
        val type: AficheType,
        var title: String,
        var image: String? = null,
        var url: String? = null,
        var htmlContent: String? = null
)