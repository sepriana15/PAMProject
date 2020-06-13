package com.bignerdranch.android.newspam.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bignerdranch.android.newspam.News

//@Database(entities = [Crime::class], version = 1)
@Database(entities = [News::class], version = 2)
@TypeConverters(NewsTypeConverters::class)
abstract class NewsDatabase : RoomDatabase() {

    abstract fun newsDao(): NewsDao
}

val migration_1_2 = object : Migration(1,2){
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE News ADD COLUMN suspect TEXT NOT NULL DEFAULT ''"
        )
    }
}