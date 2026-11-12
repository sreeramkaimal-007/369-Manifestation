package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.model.Affirmation
import com.example.model.DailyLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Affirmation::class, DailyLog::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun affirmationDao(): AffirmationDao
    abstract fun dailyLogDao(): DailyLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "affirmation_369_database"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialAffirmations(database.affirmationDao())
                    }
                }
            }

            suspend fun populateInitialAffirmations(dao: AffirmationDao) {
                val starterAffirmations = listOf(
                    Affirmation(
                        text = "I am a powerful creator, and everything I desire is flowing into my life right now.",
                        category = "Abundance",
                        intentionNote = "Aligning with Nikola Tesla's divine frequency of abundance.",
                        isActive = true
                    ),
                    Affirmation(
                        text = "Money flows to me easily, frequently, and in expanding quantities from multiple sources.",
                        category = "Abundance",
                        intentionNote = "Visualizing prosperity and unshakeable financial freedom.",
                        isActive = false
                    ),
                    Affirmation(
                        text = "I radiate warmth and love; I attract kind, genuine, and supportive connections.",
                        category = "Love",
                        intentionNote = "Opening the heart chakra to soul connections.",
                        isActive = false
                    ),
                    Affirmation(
                        text = "Every cell in my body vibrates with vitality, radiant health, and boundless energy.",
                        category = "Health",
                        intentionNote = "Focusing on whole-body restoration and cellular rejuvenation.",
                        isActive = false
                    ),
                    Affirmation(
                        text = "I am worthy of profound success, and my unique talents bring immense value to the world.",
                        category = "Career",
                        intentionNote = "Stepping boldly into professional mastery and creative fulfillment.",
                        isActive = false
                    ),
                    Affirmation(
                        text = "I am in complete harmony with the universe. Peace, clarity, and trust guide my every step.",
                        category = "Inner Peace",
                        intentionNote = "Releasing anxiety and resting in peaceful presence.",
                        isActive = false
                    )
                )
                dao.insertAll(starterAffirmations)
            }
        }
    }
}
