package net.alkafeel.questions

import androidx.room.Dao
import androidx.room.Query

@Dao
interface AnswerDao {
    @Query("SELECT * FROM answers WHERE question_id = :questionId")
    suspend fun getAnswersForQuestion(questionId: Int): List<Answer>
}
