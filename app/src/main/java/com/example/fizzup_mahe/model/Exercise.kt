package com.example.fizzup_mahe.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "exercise_table")
data class Exercise(
    @SerializedName("id")
    @PrimaryKey
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("image_url")
    @ColumnInfo(name = "image_url")
    val imageUrl: String
)