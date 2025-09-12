package com.apc.cng_hpcl.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.apc.cng_hpcl.databinding.SpeechFragBinding
import com.apc.cng_hpcl.util.ManagePermissions


class SpeechFrag: Fragment() {
    private lateinit var mContext: Context
    private lateinit var navController: NavController
    private lateinit var binding: SpeechFragBinding
    private lateinit var managePermissions: ManagePermissions
    private val permissionsRequestCode = 123


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext=context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=SpeechFragBinding.inflate(inflater)
        return binding.root
    }
    private var activityResultLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()) {result ->
            var allAreGranted = true
            for(b in result.values) {
                allAreGranted = allAreGranted && b
            }

            if(allAreGranted) {

                val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

                val recognitionListener: RecognitionListener = object : RecognitionListener {
                    override fun onReadyForSpeech(bundle: Bundle) {
                        // Called when the user starts speaking
                    }

                    override fun onBeginningOfSpeech() {
                        // Called when the user starts speaking
                    }

                    override fun onRmsChanged(v: Float) {
                        // Called when the input sound level changes
                    }

                    override fun onBufferReceived(bytes: ByteArray) {
                        // Called when the audio buffer has been received
                    }

                    override fun onEndOfSpeech() {
                        // Called when the user stops speaking
                    }

                    override fun onError(i: Int) {
                        // Called when an error occurs
                    }

                    override fun onResults(bundle: Bundle) {
                        // Called when the recognition results are ready
                        val matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches!![0]
                        binding.speechTv.text = text
                        Toast.makeText(mContext,"Stopped",Toast.LENGTH_LONG).show()
                        speechRecognizer.stopListening()

                        // Use the text for further processing
                    }

                    override fun onPartialResults(bundle: Bundle) {
                        // Called when partial recognition results are available
                    }

                    override fun onEvent(i: Int, bundle: Bundle) {
                        // Called when an event occurs
                    }
                }

                speechRecognizer.setRecognitionListener(recognitionListener)
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                speechRecognizer.startListening(intent)

            }
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController=Navigation.findNavController(view)

        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

        val recognitionListener: RecognitionListener = object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle) {
                // Called when the user starts speaking
            }

            override fun onBeginningOfSpeech() {
                // Called when the user starts speaking
            }

            override fun onRmsChanged(v: Float) {
                // Called when the input sound level changes
            }

            override fun onBufferReceived(bytes: ByteArray) {
                // Called when the audio buffer has been received
            }

            override fun onEndOfSpeech() {
                // Called when the user stops speaking
            }

            override fun onError(i: Int) {
                // Called when an error occurs
            }

            override fun onResults(bundle: Bundle) {
                // Called when the recognition results are ready
                val matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches!![0]
                binding.speechTv.text = text
                Toast.makeText(mContext,"Stopped",Toast.LENGTH_LONG).show()
                speechRecognizer.stopListening()

                // Use the text for further processing
            }

            override fun onPartialResults(bundle: Bundle) {
                // Called when partial recognition results are available
            }

            override fun onEvent(i: Int, bundle: Bundle) {
                // Called when an event occurs
            }
        }

        speechRecognizer.setRecognitionListener(recognitionListener)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )





            binding.speechBt.setOnClickListener {
                if (ActivityCompat.checkSelfPermission(mContext,
                        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(mContext, "Listening", Toast.LENGTH_LONG).show()
                    speechRecognizer.startListening(intent)
                }
                else {
                    val appPerms: Array<String> = arrayOf(
                        Manifest.permission.RECORD_AUDIO
                    )
                    activityResultLauncher.launch(appPerms)
                }

            }




    }
}