package com.deadlineshooters.yudemy.helpers

import com.algolia.search.client.ClientSearch
import com.algolia.search.dsl.attributesToRetrieve
import com.algolia.search.dsl.query
import com.algolia.search.helper.deserialize
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.IndexName
import com.algolia.search.model.ObjectID
import com.deadlineshooters.yudemy.models.AlgoliaCourse
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.repositories.CourseRepository

class SearchHelper {

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
                avgRating = course.avgRating,
                price = course.price,
                thumbnail = course.thumbnail.secure_url,
                _highlightResult = null,
                objectID = ObjectID(course.id),
            )
            saveObject(AlgoliaCourse.serializer(), record).wait()
        }
    }

    suspend fun searchIndex(s: String) {
        val query = query {
            query = s
            hitsPerPage = 50
            attributesToRetrieve {"objectID"}
        }
        val result = index.search(query)

        result.hits.deserialize(AlgoliaCourse.serializer())
    }
}



