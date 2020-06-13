package com.bignerdranch.android.newspam

import androidx.lifecycle.ViewModel

class NewsListViewModel : ViewModel() {
//    val crimes = mutableListOf<Crime>()
//
//    init {
//        for (i in 0 until 100) {
//            val crime = Crime()
//            crime.title = "Crime #$i"
//            crime.isSolved = i % 2 == 0
//            crimes += crime
//        }
//    }

    private val newsRepository = NewsRepository.get()
//  val crime = crimeRepository.getCrimes()
    val newsListLiveData = newsRepository.getNews()

    fun addNews(news: News){
        newsRepository.addNews(news)
    }
}