package com.example.fakefinder.ui.Home

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.fakefinder.R
import com.example.fakefinder.utils.VAL_GET_NAME_FAILED
import com.example.fakefinder.databinding.FragmentHomeBinding
import com.example.fakefinder.ui.Activities.AuthActivity
import com.example.fakefinder.ui.Activities.VoiceDetectionActivity
import com.example.fakefinder.ui.Activities.VoiceFilterActivity
import com.example.fakefinder.ui.Activities.VoiceGenerationActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    @Inject
    lateinit var auth: FirebaseAuth
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeFragmentViewModel: HomeFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeFragmentViewModel.getUserName()
        _binding = FragmentHomeBinding.bind(view)
        onClicks()
        observers()
    }

    private fun observers() {
        homeFragmentViewModel.userName.observe(viewLifecycleOwner) {
            if (it.equals(VAL_GET_NAME_FAILED)) {
                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
            } else {
                binding.userName.text = it
            }
        }
    }

    private fun onClicks() {
        binding.logoutButton.setOnClickListener {
            AlertDialog.Builder(activity)
                .setMessage("Log out of your account?")
                .setPositiveButton(
                    "LOG OUT"
                ) { p0, p1 ->
                    auth.signOut()
                    val intent = Intent(activity, AuthActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }.setNegativeButton(
                    "CANCEL"
                ) { p0, p1 -> }
                .create()
                .show()
        }
        binding.voiceDetCardView.setOnClickListener {
            val intent = Intent(activity, VoiceDetectionActivity::class.java)
            startActivity(intent)
        }
        binding.voiceGenCardView.setOnClickListener {
            val intent = Intent(activity, VoiceGenerationActivity::class.java)
            startActivity(intent)
        }
        binding.voiceFilterCardView.setOnClickListener {
            val intent = Intent(activity, VoiceFilterActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}