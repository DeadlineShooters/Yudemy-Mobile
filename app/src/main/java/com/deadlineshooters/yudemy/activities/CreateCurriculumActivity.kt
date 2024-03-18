package com.deadlineshooters.yudemy.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.adapters.SectionsAddedAdapter
import com.deadlineshooters.yudemy.models.Lecture
import com.deadlineshooters.yudemy.models.Section
import com.deadlineshooters.yudemy.models.Video
import java.util.ArrayList

class CreateCurriculumActivity : AppCompatActivity() {
    private lateinit var toolbarCreateCurriculum: Toolbar
    private lateinit var addedSections: RecyclerView
    private lateinit var btnAddSection: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_curriculum)

        toolbarCreateCurriculum = findViewById(R.id.toolbar_create_curriculum)
        addedSections = findViewById(R.id.addedSections)
        btnAddSection = findViewById(R.id.btnAddSection)

        setupActionBar()

        val listSection = createDummyDataSection()
        val listLecture = createDummyDataLecture()

        val sectionAdapter = SectionsAddedAdapter(listSection)
        addedSections.adapter = sectionAdapter
        addedSections.layoutManager = LinearLayoutManager(this)

        btnAddSection.setOnClickListener {
            val section = Section("", "", sectionAdapter.itemCount + 1)
            listSection.add(section)
            sectionAdapter.notifyItemInserted(listSection.size - 1)
            addedSections.smoothScrollToPosition(sectionAdapter.itemCount - 1)
        }

        sectionAdapter.onAddLectureClick = {adapter, position ->
            val newLecture = Lecture("", listSection[position]._id, Video(), "", "", adapter.itemCount + 1)
            adapter.lectures.add(newLecture)
            adapter.notifyItemInserted(adapter.itemCount - 1)
            addedSections.smoothScrollToPosition(position)
        }
        sectionAdapter.onDeleteSectionClick = {
            listSection.removeAt(it)
            sectionAdapter.notifyItemRemoved(it)
            addedSections.smoothScrollToPosition(it)
        }
        sectionAdapter.onDeleteLectureClick = {adapter, positionLecture, positionSection ->
            adapter.lectures.removeAt(positionLecture)
            adapter.notifyItemRemoved(positionLecture)
            addedSections.smoothScrollToPosition(positionSection)
        }
    }

    fun createDummyDataSection(): ArrayList<Section> {
        val sections = ArrayList<Section>()
        for(i in 1..5) {
            val l = Section("1", "Introduction", 1)
            l.index = i
            sections.add(l)
        }
        return sections
    }

    fun createDummyDataLecture(): ArrayList<Lecture> {
        val lectures = ArrayList<Lecture>()
        for(i in 1..5) {
            val l = Lecture("1", "1", Video(), "abc", "Video", 1)
            l._id = i.toString()
            l.index = i
            lectures.add(l)
        }
        return lectures
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbarCreateCurriculum)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbarCreateCurriculum.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }
}