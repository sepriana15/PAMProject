package com.bignerdranch.android.newspam.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.bignerdranch.android.newspam.News
import java.util.*

@Dao
interface NewsDao {
    @Query("SELECT * FROM news")
    fun getNews(): LiveData<List<News>>

    @Query("SELECT * FROM news WHERE id=(:id)")
    fun getNews(id: UUID): LiveData<News?>

    @Update
    fun updateNews(news: News)

    @Insert
    fun addNews(news: News)
}