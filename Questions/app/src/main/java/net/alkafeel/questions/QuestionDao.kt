package net.alkafeel.questions

import androidx.room.Dao
import androidx.room.Query

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions WHERE category_id = :categoryId")
    suspend fun getQuestionsForCategory(categoryId: Int): List<Question>
}
