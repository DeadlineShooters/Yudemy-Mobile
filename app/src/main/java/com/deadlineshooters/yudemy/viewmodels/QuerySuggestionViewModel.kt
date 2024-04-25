package com.deadlineshooters.yudemy.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.algolia.instantsearch.searchbox.SearchBoxConnector
import com.algolia.instantsearch.searcher.hits.addHitsSearcher
import com.algolia.instantsearch.searcher.multi.MultiSearcher
import com.algolia.search.client.ClientSearch
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.IndexName
import com.deadlineshooters.yudemy.models.Suggestion

class QuerySuggestionViewModel : ViewModel() {

    private val client = ClientSearch(
        applicationID = ApplicationID("6ZF9V4WNWQ"),
        apiKey = APIKey("a3277b74e7356f932541bb8b4ee14074"),
    )
    private val multiSearcher = MultiSearcher(client)
    val courseSearcher = multiSearcher.addHitsSearcher(indexName = IndexName("yudemy_courses"))
    val suggestionSearcher = multiSearcher.addHitsSearcher(indexName = IndexName("yudemy_courses_query_suggestions"))
    val searchBox = SearchBoxConnector(multiSearcher)
    val suggestions = MutableLiveData<Suggestion>()

    override fun onCleared() {
        multiSearcher.cancel()
        searchBox.disconnect()
        client.close()
    }
}
