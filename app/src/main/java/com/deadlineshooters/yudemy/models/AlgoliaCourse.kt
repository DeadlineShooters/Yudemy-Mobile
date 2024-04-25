package com.deadlineshooters.yudemy.models

import com.algolia.instantsearch.core.highlighting.HighlightedString
import com.algolia.instantsearch.highlighting.Highlightable
import com.algolia.search.model.Attribute
import com.algolia.search.model.ObjectID
import com.algolia.search.model.indexing.Indexable
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class AlgoliaCourse(
    var name: String = "",
    var instructor: String = "",
    var introduction: String = "",
    var description: String = "",
    var category: String = "",
    var price: Double = 0.0,
    var avgRating: Double = 0.0,
    var thumbnail: String = "",
    override val _highlightResult: JsonObject?,
    override val objectID: ObjectID,
) : Indexable, Highlightable {

    val highlightedName: HighlightedString?
        get() = getHighlight(Attribute("name"))
}
