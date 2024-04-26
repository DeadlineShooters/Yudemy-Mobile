package com.deadlineshooters.yudemy.models

import com.algolia.instantsearch.core.highlighting.HighlightedString
import com.algolia.instantsearch.highlighting.Highlightable
import com.algolia.search.model.Attribute
import com.algolia.search.model.ObjectID
import com.algolia.search.model.indexing.Indexable
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import java.text.SimpleDateFormat
import java.util.*

@Serializable
data class AlgoliaCourse(
    var name: String = "",
    var instructor: String = "",
    var category: String = "",
    var introduction: String = "",
    var description: String = "",
    var language: String = "",
    var price: Double = 0.0,
    var avgRating: Double = 0.0,
    var totalLength: Int = 0,
    var createdDate: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
    var thumbnail: String = "",
    override val _highlightResult: JsonObject?,
    override val objectID: ObjectID,
) : Indexable, Highlightable {

    val highlightedName: HighlightedString?
        get() = getHighlight(Attribute("name"))
}
