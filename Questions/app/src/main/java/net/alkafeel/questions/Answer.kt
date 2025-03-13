package net.alkafeel.questions

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "answers")
data class Answer(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "question_id") val questionId: Int,
    @ColumnInfo(name = "answer_text") val answerText: String,
    @ColumnInfo(name = "is_correct") val isCorrect: Boolean
)
