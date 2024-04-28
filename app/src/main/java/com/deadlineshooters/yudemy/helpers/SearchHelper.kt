package com.deadlineshooters.yudemy.helpers

import com.algolia.search.client.ClientSearch
import com.algolia.search.dsl.attributesToRetrieve
import com.algolia.search.dsl.query
import com.algolia.search.helper.deserialize
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.IndexName
import com.algolia.search.model.ObjectID
import com.deadlineshooters.yudemy.BuildConfig
import com.deadlineshooters.yudemy.models.AlgoliaCourse
import com.deadlineshooters.yudemy.models.Course
import com.deadlineshooters.yudemy.repositories.CourseRepository

class SearchHelper {

    val courseRepository = CourseRepository()

    // Connect and authenticate with your Algolia app
    private val client = ClientSearch(
        applicationID = ApplicationID("6ZF9V4WNWQ"),
        apiKey = APIKey(BuildConfig.ALGOLIA_API_KEY),
    )


    val index = client.initIndex(indexName = IndexName("yudemy_courses"))

    suspend fun indexData(course: Course) {
        index.run {
            val record = AlgoliaCourse(
                name = course.name,
                instructor = course.instructor,
                category = course.category,
                introduction = course.introduction,
                description = course.description,
                language = course.language,
                price = course.price,
                avgRating = course.avgRating,
                totalLength = course.totalLength,
                createdDate = course.createdDate,
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



