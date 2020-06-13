package com.bignerdranch.android.newspam

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
//import com.bignerdranch.android.newspam.database.NewsDatabase
import com.bignerdranch.android.newspam.database.NewsDatabase
import com.bignerdranch.android.newspam.database.migration_1_2
import java.io.File
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "news-database"

class NewsRepository private constructor(context: Context) {

    private val database: NewsDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            NewsDatabase::class.java,
            DATABASE_NAME
        //).build()
        ).addMigrations(migration_1_2).build()
    private val newsDao = database.newsDao()
    private val executor = Executors.newSingleThreadExecutor()
    private val filesDir = context.applicationContext.filesDir

    fun getNews(): LiveData<List<News>> = newsDao.getNews()

    fun getNews(id: UUID): LiveData<News?> = newsDao.getNews(id)

    fun updateNews(news: News) {
        executor.execute {
            newsDao.updateNews(news)
        }
    }

    fun addNews(news: News) {
        executor.execute {
            newsDao.addNews(news)
        }
    }
    fun getPhotoFile(news: News):File = File(filesDir, news.photoFileName)

    companion object {
        private var INSTANCE: NewsRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = NewsRepository(context)
            }
        }

        fun get(): NewsRepository {
            return INSTANCE ?: throw IllegalStateException("NewsRepository must be initialized")
        }
    }
}