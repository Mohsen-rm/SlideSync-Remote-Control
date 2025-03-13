package net.alkafeel.questions

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Question::class, Answer::class], version = 6)
abstract class AppDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao
    abstract fun answerDao(): AnswerDao
}
