package com.bignerdranch.android.newspam

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

private const val TAG = "NewsListFragment"

class NewsListFragment<NewsAdapter> : Fragment() {

    interface Callbacks {
//        fun onNewsSelected(newsId: UUID)
        fun UUID.onNewsSelected()

    }

    private var callbacks: Callbacks? = null

    private lateinit var newsRecyclerView: RecyclerView
//    private var adapter: NewsAdapter? = NewsAdapter(emptyList())
        set(value) = TODO()

    private val newsListViewModel: NewsListViewModel by lazy {
        ViewModelProviders.of(this).get(NewsListViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    companion object {
        fun newInstance(): NewsListFragment<Any?> {
            return NewsListFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_news_list, container, false)
        newsRecyclerView = view.findViewById(R.id.news_recycler_view) as RecyclerView
        newsRecyclerView.layoutManager = LinearLayoutManager(context)
//        newsRecyclerView.adapter = adapter

        //updateUI()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newsListViewModel.newsListLiveData.observe(
            viewLifecycleOwner,
            Observer { news ->
                news?.let {
                    Log.i(TAG, "Got news ${news.size}")
                    updateUI(news)
                }
            }
        )
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_news_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.news_news -> {
                val news = News()
                newsListViewModel.addNews(news)
                callbacks?.onNewsSelected(news.id)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun updateUI(news: List<News>) {
//        adapter = NewsAdapter(news)
//        newsRecyclerView.adapter = adapter
    }

    private inner class NewsHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        private lateinit var news: News

        private val titleTextView: TextView = itemView.findViewById(R.id.news_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.news_date)
        private val solvedImageView: ImageView = itemView.findViewById(R.id.news_solved);

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(news: News) {
            this.news = news
            titleTextView.text = this.news.title
            dateTextView.text = this.news.date.toString()
            solvedImageView.visibility = if (news.isSolved) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        override fun onClick(v: View?) {
            //Toast.makeText(context, "${crime.title} pressed!", Toast.LENGTH_SHORT).show()
            callbacks?.onNewsSelected(news.id)
        }
    }

    private inner class CrimeAdapter(var news: List<News>) :
        RecyclerView.Adapter<NewsHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsHolder {
            val view = layoutInflater.inflate(R.layout.list_item_news, parent, false)
            return NewsHolder(view)
        }

        override fun onBindViewHolder(holder: NewsHolder, position: Int) {
            val news = news[position]
            holder.bind(news)
        }

        override fun getItemCount() = news.size

    }
}