package com.bignerdranch.android.newspam

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.io.File
import java.util.*

class NewsDetailViewModel() : ViewModel() {

    private val newsRepository = NewsRepository.get()
    private val newsIdLiveData = MutableLiveData<UUID>()

    var newsLiveData: LiveData<News?> =
        Transformations.switchMap(newsIdLiveData) { newsId ->
            newsRepository.getNews(newsId)
        }

    fun loadNews(newsId: UUID) {
        newsIdLiveData.value = newsId
    }

    fun saveNews(news: News) {
        newsRepository.updateNews(news)
    }
    fun getPhotoFile(news: News): File {
        return newsRepository.getPhotoFile(news)
    }
}