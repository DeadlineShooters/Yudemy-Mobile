package com.deadlineshooters.yudemy.helpers

import com.algolia.search.client.ClientSearch
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.IndexName
import com.algolia.search.model.ObjectID
import com.algolia.search.model.indexing.Indexable
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.repositories.CourseRepository
import kotlinx.serialization.Serializable

class SearchHelper {
    @Serializable
    data class AlgoliaCourse(
        var name: String = "",
        var instructor: String = "",
        var introduction: String = "",
        var description: String = "",
        var category: String = "",
        override val objectID: ObjectID
    ) : Indexable

    val courseRepository = CourseRepository()

    // Connect and authenticate with your Algolia app
    val client = ClientSearch(
        applicationID = ApplicationID("6ZF9V4WNWQ"),
        apiKey = APIKey("a3277b74e7356f932541bb8b4ee14074")
    )

    val index = client.initIndex(indexName = IndexName("yudemy_courses"))

    suspend fun indexData(course: Course) {
        index.run {
            val record = AlgoliaCourse(
                name = course.name,
                instructor = course.instructor,
                introduction = course.introduction,
                description = course.description,
                category = course.category,
                ObjectID(course.id),
            )
            saveObject(AlgoliaCourse.serializer(), record).wait()
        }
    }
}



