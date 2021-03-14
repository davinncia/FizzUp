package com.example.fizzup_mahe.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fizzup_mahe.R
import com.example.fizzup_mahe.model.Exercise

class ExerciseDiffCallBack : DiffUtil.ItemCallback<Exercise>() {
    override fun areItemsTheSame(oldItem: Exercise, newItem: Exercise): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Exercise, newItem: Exercise): Boolean {
        return oldItem == newItem
    }
}

class ExerciseAdapter: ListAdapter<Exercise, ExerciseAdapter.ExerciseViewHolder>(ExerciseDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise, parent, false)

        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ExerciseViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val imageView = itemView.findViewById<ImageView>(R.id.iv_exercise_item)
        private val nameView = itemView.findViewById<TextView>(R.id.tv_exercise_name_item)

        init {
            itemView.findViewById<ImageView>(R.id.iv_arrow_right_exercise_item).setOnClickListener {
                Toast.makeText(itemView.context, getItem(adapterPosition).name, Toast.LENGTH_SHORT).show()
            }
        }

        fun bind(item: Exercise) {
            Glide.with(imageView.context).load(item.imageUrl).into(imageView)
            nameView.text = item.name
        }
    }
}