package com.deadlineshooters.yudemy.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.models.Question
import com.deadlineshooters.yudemy.models.Reply
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReplyListAdapter(val replyList: List<Reply>) : RecyclerView.Adapter<ReplyListAdapter.ViewHolder>() {
    var onItemClick: ((Reply) -> Unit)? = null
    private val originalFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val newFormat = SimpleDateFormat("dd, MMM, yyyy", Locale.getDefault())

    inner class ViewHolder(listReplyView: View) : RecyclerView.ViewHolder(listReplyView) {
        val replierImage: ImageView = listReplyView.findViewById(R.id.replierImage)
        val replierName: TextView = listReplyView.findViewById(R.id.replierName)
        val replyDate: TextView = listReplyView.findViewById(R.id.replyDate)
        val replyContentView: ConstraintLayout = listReplyView.findViewById(R.id.replyContentView)
        val replyContent: TextView = listReplyView.findViewById(R.id.replyContent)
        val replyImage: ImageView = listReplyView.findViewById(R.id.replyImage)

        init {
            listReplyView.setOnClickListener {
                onItemClick?.invoke(replyList[bindingAdapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val courseView = inflater.inflate(R.layout.reply_list_item, parent, false)
        return ViewHolder(courseView)
    }

    override fun getItemCount(): Int {
        return replyList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reply: Reply = replyList[position]


        val replierImage = holder.replierImage
        val replierName = holder.replierName
        val replyDate = holder.replyDate
        val replyContent = holder.replyContent
        val replyImage = holder.replyImage

        replierName.text = reply.replier

        val date: Date = originalFormat.parse(reply.createdTime) ?: Date()
        val formattedDate: String = newFormat.format(date)
        replyDate.text = formattedDate

        replyContent.text = reply.content

        if(reply.images.isEmpty()){
            replyImage.visibility = View.GONE
        }
        else{
            replyImage.visibility = View.VISIBLE
        }
    }
}