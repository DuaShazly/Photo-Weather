package com.robusta.photoweather.ui.fragments

import android.graphics.BitmapFactory
import android.media.ThumbnailUtils

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.navigation.fragment.NavHostFragment
import com.robusta.photoweather.CAPTURE_TO_RESULT_PICTURE
import com.robusta.photoweather.CAPTURE_TO_RESULT_PICTURE_URI
import com.robusta.photoweather.R
import com.robusta.photoweather.data.HistoryItem
import com.robusta.photoweather.databinding.FragmentHistoryBinding
import com.robusta.photoweather.ui.adapter.HistoryAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val view = binding.root

        val list: ArrayList<HistoryItem> = ArrayList()

        var file = context?.cacheDir

        CoroutineScope(Dispatchers.IO).launch{
            for (f in file?.listFiles()!!)
            {
                list.apply {
                    val bitmap = BitmapFactory.decodeFile(f.absolutePath)
                    val thumbnail = ThumbnailUtils.extractThumbnail(bitmap,100,100)
                    val lastModifiedDate: Date =  Date(f.lastModified())
                    if(thumbnail!=null) add(HistoryItem(thumbnail,lastModifiedDate.toString(),f.absolutePath))
                }

            }
            withContext(Dispatchers.Main) {
                val historyAdapter = HistoryAdapter(list) { link: HistoryItem ->
                    historyItemClicked(
                        link
                    )
                }
                binding.historyRecyclerView.adapter = historyAdapter
            }
        }

        return view
    }

    private fun historyItemClicked(historyItem: HistoryItem) {
        val bundle = Bundle()
        bundle.putParcelable(CAPTURE_TO_RESULT_PICTURE, BitmapFactory.decodeFile(historyItem.absolutePath))
        bundle.putString(
            CAPTURE_TO_RESULT_PICTURE_URI,
            FileProvider.getUriForFile(requireContext(),"com.robusta.photoweather.fileprovider", File(historyItem.absolutePath))
                .toString()
        )

        NavHostFragment.findNavController(this@HistoryFragment)
            .navigate(R.id.action_historyFragment_to_resultFragment2, bundle)
    }

}