package com.bignerdranch.android.newspam

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import android.text.format.DateFormat
import android.widget.*
import androidx.core.content.FileProvider
import java.io.File
import java.util.*

private const val TAG = "NewsFragment"
private const val ARG_NEWS_ID = "news_id"
private const val DIALOG_DATE = "DialogDate"
private const val  REQUEST_DATE = 0
private const val REQUEST_CONTACT = 1
private const val REQUEST_PHOTO = 2
private const val DATE_FORMAT = "EEE, MMM, dd"
class NewsFragment : Fragment(), DatePickerFragment.Callbacks {
    private lateinit var news: News
    private lateinit var photoFile:File
    private lateinit var photoUri: Uri
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var reportButton: Button
    private lateinit var suspectButton: Button
    private lateinit var photoButton: ImageButton
    private lateinit var photoView: ImageView

    private val newsDetailViewModel: NewsDetailViewModel by lazy {
        ViewModelProviders.of(this).get(NewsDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        news = News()
        val newsId: UUID = arguments?.getSerializable(ARG_NEWS_ID) as UUID
        //Log.d(TAG, "args bundle crime ID: $crimeId")
        newsDetailViewModel.loadNews(newsId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_news, container, false)

        titleField = view.findViewById(R.id.news_title) as EditText
        dateButton = view.findViewById(R.id.news_date) as Button
        solvedCheckBox = view.findViewById(R.id.news_solved) as CheckBox
        reportButton = view.findViewById(R.id.news_report) as Button
        suspectButton = view.findViewById(R.id.news_suspect) as Button
        photoButton = view.findViewById(R.id.news_camera) as ImageButton
        photoView = view.findViewById(R.id.news_photo) as ImageView
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newsDetailViewModel.newsLiveData.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { news ->
                news?.let {
                    this.news = news
                    photoFile = newsDetailViewModel.getPhotoFile(news)
                    photoUri = FileProvider.getUriForFile(
                        requireActivity(),
                         "com.bignerdranch.android.newspam.fileprovider",
                          photoFile
                        )
                    updateUI()
                }
            })
    }

    override fun onStart() {
        super.onStart()
        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This line intentionally left blank
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                news.title = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                // This line intentionally left blank
            }
        }

        titleField.addTextChangedListener(titleWatcher)

        solvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked -> news.isSolved = isChecked }
        }
        suspectButton.apply {
            val pickContactIntent =
                Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            setOnClickListener {
                startActivityForResult(pickContactIntent, REQUEST_CONTACT)
            }
            val packageManager: PackageManager = requireActivity().packageManager
            val resolvedActivity: ResolveInfo? =
                packageManager.resolveActivity(pickContactIntent, PackageManager.MATCH_DEFAULT_ONLY)
            if (resolvedActivity == null) {
                isEnabled = false
            }
        }
        photoButton.apply {
            val packageManager:PackageManager = requireActivity().packageManager
            val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val resolvedActivity: ResolveInfo?= packageManager.resolveActivity(captureImage,
            PackageManager.MATCH_DEFAULT_ONLY)
            if (resolvedActivity == null)
            {
               isEnabled = false
            }
            setOnClickListener {
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                val cameraActivities:List<ResolveInfo> =
                    packageManager.queryIntentActivities(captureImage,
                    PackageManager.MATCH_DEFAULT_ONLY)

                for (cameraActivity in cameraActivities){
                    requireActivity().grantUriPermission(
                        cameraActivity.activityInfo.packageName,
                        photoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }
                    startActivityForResult(captureImage, REQUEST_PHOTO)
            }

            }

        dateButton.setOnClickListener {
            // DatePickerFragment().apply {
            DatePickerFragment.newInstance(news.date).apply {
                setTargetFragment(this@NewsFragment, REQUEST_DATE)
                show(this@NewsFragment.requireFragmentManager(), DIALOG_DATE)
            }
        }
        reportButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, newsReport)
                putExtra(
                    Intent.EXTRA_SUBJECT,
                    getString(R.string.news_report_subject)
                )
            }.also { intent ->
                // startActivity(intent)
                val chooserIntent = Intent.createChooser(intent, getString(R.string.send_report))
                startActivity(chooserIntent)

            }
        }
    }

    private fun getNewsReport(): Bundle? {
        TODO("Not yet implemented")
    }

    override fun onStop() {
        super.onStop()
        newsDetailViewModel.saveNews(news)
    }

    override fun onDetach() {
        super.onDetach()
        requireActivity().revokeUriPermission(photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    }

    override fun onDateSelected(date: Date) {
        news.date = date
        updateUI()
    }

    private fun updateUI() {
        titleField.setText(news.title)
        dateButton.text = news.date.toString()
        // solvedCheckBox.isChecked = crime.isSolved
        solvedCheckBox.apply {
            isChecked = news.isSolved
            jumpDrawablesToCurrentState()
        }
        if (news.suspect.isNotEmpty()) {
            suspectButton.text = news.suspect
        }
        updatePhotoView()
    }
    private fun updatePhotoView(){
        if (photoFile.exists()){
            val bitmap = getScaledBitmap(photoFile.path, requireActivity() as MainActivity)
            photoView.setImageBitmap(bitmap)
        }
        else {
            photoView.setImageDrawable(null)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
    when {
        resultCode != Activity.RESULT_OK -> return
        requestCode == REQUEST_CONTACT && data != null -> {
        val contactUri: Uri? = data.data
        val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
        val cursor = contactUri?.let {
            requireActivity().contentResolver
                .query(it, queryFields, null, null, null)
        }
        cursor?.use {
            if (it.count == 0) {
                return
            }
            it.moveToFirst()
            val suspect = it.getString(0)
            news.suspect = suspect
            newsDetailViewModel.saveNews(news)
            suspectButton.text = suspect
            }
        }
        requestCode == REQUEST_PHOTO -> {
            requireActivity().revokeUriPermission(photoUri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            updatePhotoView()
        }
    }
}

    private val newsReport: String
        get() {
            val solvedString = if (news.isSolved) {
                getString(R.string.news_report_solved)
            } else {
                getString(R.string.news_report_unsolved)
            }
            val dateString = DateFormat.format(DATE_FORMAT, news.date).toString()
            var suspect = if (news.suspect.isBlank()) {
                getString(R.string.news_report_no_suspect)
            } else {
                getString(R.string.news_report_suspect, news.suspect)
            }
            return getString(
                R.string.news_report,
                news.title, dateString, solvedString, suspect
            )
        }
    companion object {
        fun newInstance(newsId: UUID): NewsFragment {
            val args = Bundle().apply {
                putSerializable(ARG_NEWS_ID, newsId)
            }
            return NewsFragment().apply {
                arguments = args
            }
        }
    }
}

