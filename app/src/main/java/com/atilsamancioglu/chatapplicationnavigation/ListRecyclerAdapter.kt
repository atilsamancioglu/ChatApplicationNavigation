package com.atilsamancioglu.chatapplicationnavigation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class ListRecyclerAdapter : RecyclerView.Adapter<ListRecyclerAdapter.ChatHolder>() {
    private val VIEW_TYPE_MESSAGE_SENT = 1
    private val VIEW_TYPE_MESSAGE_RECEIVED = 2

    class ChatHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

    }

    private val diffUtil = object : DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }

    }

    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var chats: List<Chat>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    override fun getItemViewType(position: Int): Int {
        val chat = chats.get(position)

        if(chat.user == FirebaseAuth.getInstance().currentUser?.email.toString()) {
            return VIEW_TYPE_MESSAGE_SENT
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHolder {
        if(viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.row_layout, parent, false)
            return ChatHolder(view)

        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.row_layout_right, parent, false)
            return ChatHolder(view)

        }
    }

    override fun onBindViewHolder(holder: ChatHolder, position: Int) {
        val textView = holder.itemView.findViewById<TextView>(R.id.recyclerViewTextView)
        textView.text = "${chats.get(position).user} : ${chats.get(position).text}"
    }

    override fun getItemCount(): Int {
        return chats.size
    }
}

