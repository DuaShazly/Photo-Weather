package com.robusta.photoweather.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.robusta.photoweather.CAPTURE_TO_RESULT_PICTURE
import com.robusta.photoweather.CAPTURE_TO_RESULT_PICTURE_URI
import com.robusta.photoweather.databinding.FragmentResultBinding


class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.imageView.setImageBitmap(arguments?.getParcelable(CAPTURE_TO_RESULT_PICTURE))

        binding.button.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(arguments?.getString(
                CAPTURE_TO_RESULT_PICTURE_URI
            )))
            intent.type = "image/jpeg"

            val shareIntent = Intent.createChooser(intent, "Share File")
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)

            startActivity(shareIntent)
        }

        return view
    }
}