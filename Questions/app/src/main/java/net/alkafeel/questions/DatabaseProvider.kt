package net.alkafeel.questions

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "data.db"  // اسم قاعدة البيانات؛ تأكد من أن ملف data.db موجود في assets
            )
                .createFromAsset("data.db") // تحميل القاعدة المُعبأة مسبقاً من مجلد assets
                .fallbackToDestructiveMigration() // في حال تغيير المخطط دون ترحيل مناسب، يتم إنشاء قاعدة بيانات جديدة
                .build()
            INSTANCE = instance
            instance
        }
    }
}
