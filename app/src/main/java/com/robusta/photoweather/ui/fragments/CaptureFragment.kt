package com.robusta.photoweather.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.otaliastudios.cameraview.BitmapCallback
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.PictureResult
import com.robusta.photoweather.CAPTURE_TO_RESULT_PICTURE
import com.robusta.photoweather.CAPTURE_TO_RESULT_PICTURE_URI
import com.robusta.photoweather.LOCATION_PERMISSION
import com.robusta.photoweather.R
import com.robusta.photoweather.data.response.WeatherResponse
import com.robusta.photoweather.databinding.FragmentCaptureBinding
import com.robusta.photoweather.utilty.LocationUtil
import com.robusta.photoweather.utilty.PermissionUtil
import com.robusta.photoweather.viewmodel.WeatherViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import kotlin.math.roundToInt


class CaptureFragment : Fragment() {

    private var _binding: FragmentCaptureBinding? = null
    private val binding get() = _binding!!

    private var camera: CameraView? = null

    @SuppressLint("SetTextI18n")
    private fun requestLocation() {
        if (!PermissionUtil.isLocationPermissionGranted(activity))
            PermissionUtil.requestLocationPermission(activity, LOCATION_PERMISSION)
        else sendCoordinates()

    }

    private fun sendCoordinates() {
        LocationUtil.getCurrentLocation(activity as AppCompatActivity) {
            it?.run {
                val viewModel by lazy {
                        WeatherViewModel(
                            it.latitude, it.longitude
                        )
                }

                val ICON_URL = "https://openweathermap.org/img/wn/"

                    viewModel.getWeather().observe(viewLifecycleOwner, Observer<WeatherResponse> {
                    binding.cityName.text = it.name
                    binding.temperature.text = (it.main.temp - 273).roundToInt().toString() + " Celsius"
                    binding.description.text = it.weather.get(0).description
                        val url = ICON_URL+it.weather.get(0).icon+".png"
                        Log.d("TAG", "sendCoordinates: $url")
                        context?.let { it1 ->
                            Glide.with(it1)
                                .load(url)
                                .error(android.R.drawable.stat_notify_error)
                                .fitCenter()
                                .into(binding.weatherIcon)
                        }
                    binding.cameraOverlay.visibility = VISIBLE

                    camera?.takePictureSnapshot()
                })

                LocationUtil.stopCurrentLocationUpdates(activity as AppCompatActivity)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION -> {
                if (PermissionUtil.isLocationPermissionGranted(activity)) {
                    LocationUtil.getCurrentLocation(activity as AppCompatActivity) {
                        it?.run {
                            sendCoordinates()
                        }
                    }
                } else {
                    // TODO: Handle failing to get permissions
                }

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCaptureBinding.inflate(inflater, container, false)
        val view = binding.root

        camera = binding.camera

        camera!!.setLifecycleOwner(viewLifecycleOwner)

        binding.captureBtn.setOnClickListener {
            binding.captureBtn.visibility = INVISIBLE
            binding.loading.visibility = VISIBLE
            if (this.context?.let { checkForInternet(it) } == true) {
                requestLocation()

            } else {
                Toast.makeText(this.context, "No Internet Connetion", Toast.LENGTH_SHORT).show()
                binding.captureBtn.visibility = VISIBLE
                binding.loading.visibility = INVISIBLE
            }
        }

        camera?.addCameraListener(mCameraListener)

        return view
    }

    private var mCameraListener = object : CameraListener() {
        override fun onPictureTaken(result: PictureResult) {
            super.onPictureTaken(result)
            result.toBitmap(BitmapCallback {
                val uri = saveImageToInternalStorage(it!!)
                val bundle = Bundle()
                bundle.putParcelable(CAPTURE_TO_RESULT_PICTURE, it)
                bundle.putString(CAPTURE_TO_RESULT_PICTURE_URI, uri.toString())
this@CaptureFragment.findNavController()
    .navigate(R.id.action_captureFragment_to_resultFragment, bundle)
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun saveImageToInternalStorage(image: Bitmap): Uri {

        var file = context?.cacheDir

        file = File(file, "${UUID.randomUUID()}.jpeg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            image.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException){
            e.printStackTrace()
        }
        return FileProvider.getUriForFile(requireContext(),"com.robusta.photoweather.fileprovider",file)
    }


    private fun checkForInternet(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
}