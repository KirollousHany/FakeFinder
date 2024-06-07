package com.example.fakefinder.ui.Profile.ProfileInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.fakefinder.utils.EMAIL
import com.example.fakefinder.utils.FULL_NAME
import com.example.fakefinder.utils.GENDER
import com.example.fakefinder.utils.IMG
import com.example.fakefinder.R
import com.example.fakefinder.utils.USERNAME
import com.example.fakefinder.databinding.FragmentProfileBinding
import com.example.fakefinder.ui.Profile.BottomSheets.EmailUpdate.FirstDialog.EmailUpdateDialog
import com.example.fakefinder.ui.Profile.BottomSheets.FullNameUpdate.FullNameUpdateDialog
import com.example.fakefinder.ui.Profile.BottomSheets.GenderUpdate.GenderUpdateDialog
import com.example.fakefinder.ui.Profile.BottomSheets.PasswordUpdate.FirstDialog.PasswordUpdateFirstDialog
import com.example.fakefinder.ui.Profile.BottomSheets.ProfilePicUpdate.ProfilePicUpdateDialog
import com.example.fakefinder.ui.Profile.BottomSheets.UserNameUpdate.UsernameUpdateDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val profileFragmentViewModel: ProfileFragmentViewModel by viewModels()
    private var email: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)
        profileFragmentViewModel.getUserData()
        onClicks()
        observers()
    }

    private fun onClicks() {
        binding.fullNameLayout.setOnClickListener {
            activity?.let { it1 ->
                FullNameUpdateDialog.getIncs().show(it1.supportFragmentManager, "TAG")
            }
        }
        binding.userNameLayout.setOnClickListener {
            activity?.let { it1 ->
                UsernameUpdateDialog.getIncs().show(it1.supportFragmentManager, "TAG1")
            }
        }
        binding.genderLayout.setOnClickListener {
            activity?.let { it1 ->
                GenderUpdateDialog.getIncs().show(it1.supportFragmentManager, "TAG2")
            }
        }
        binding.resetPassLayout.setOnClickListener {
            activity?.let { it1 ->
                PasswordUpdateFirstDialog.getIncs(email!!).show(it1.supportFragmentManager, "TAG2")
            }
        }
        binding.profilePic.setOnClickListener {
            activity?.let { it1 ->
                ProfilePicUpdateDialog.getIncs().show(it1.supportFragmentManager, "TAG2")
            }
        }
        binding.emailLayout.setOnClickListener {
            activity?.let { it1 ->
                EmailUpdateDialog.getIncs(email!!).show(it1.supportFragmentManager, "TAG2")
            }
        }

    }

    private fun observers() {
        profileFragmentViewModel.userInfo.observe(viewLifecycleOwner) {
            if (it!!.equals(null)) {
                Toast.makeText(
                    activity,
                    "Data retrieval issue. Please restart the app.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                binding.apply {
                    fullNameTextView.text = it.fullName
                    userNameTextView.text = it.userName
                    genderTextView.text = it.gender
                    emailTextView.text = it.email
                    email = it.email
                    Glide.with(requireContext())
                        .load(it.image)
                        .placeholder(R.drawable.profile_1)
                        .into(profilePic)

                }
            }
        }
        profileFragmentViewModel.userInfoChanged.observe(viewLifecycleOwner) {
            if (it?.key.equals(FULL_NAME)) {
                binding.fullNameTextView.text = it?.value.toString()
            } else if (it?.key.equals(USERNAME)) {
                binding.userNameTextView.text = it?.value.toString()
            } else if (it?.key.equals(GENDER)) {
                binding.genderTextView.text = it?.value.toString()
            } else if (it?.key.equals(IMG)) {
                Glide.with(requireContext())
                    .load(it?.value)
                    .placeholder(R.drawable.profile_1)
                    .into(binding.profilePic)
            } else if (it?.key.equals(EMAIL)) {
                binding.emailTextView.text = it?.value.toString()
                email = it?.value.toString()
            } else {
                Toast.makeText(activity, "There are error occurred", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}