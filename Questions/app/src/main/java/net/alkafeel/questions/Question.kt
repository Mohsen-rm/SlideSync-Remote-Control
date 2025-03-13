package net.alkafeel.questions

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class Question(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "question_text") val questionText: String,
    @ColumnInfo(name = "category_id") val categoryId: Int,
)
