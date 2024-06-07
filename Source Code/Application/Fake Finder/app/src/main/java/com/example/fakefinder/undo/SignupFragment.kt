/*package com.example.fakefinder.ui.Auth.signup

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.fakefinder.R
import com.example.fakefinder.VAL_CONFPASS_PROB
import com.example.fakefinder.VAL_DATE_PROB
import com.example.fakefinder.VAL_EMAIL_PROB
import com.example.fakefinder.VAL_IMG_PROB
import com.example.fakefinder.VAL_NAME_PROB
import com.example.fakefinder.VAL_NO_PROB
import com.example.fakefinder.VAL_PASS_PROB
import com.example.fakefinder.VAL_USERNAME_PROB
import com.example.fakefinder.databinding.FragmentSignupBinding
import com.example.fakefinder.ui.Activities.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@AndroidEntryPoint
class SignupFragment : Fragment() {

    private lateinit var getContentLauncher: ActivityResultLauncher<String>
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private var _binding: FragmentSignupBinding?= null
    private val binding get() = _binding!!
    private var fileImageUser: Uri? = null
    private val signupFragmentViewModel : SignupFragmentViewModel by viewModels()
    private var userAge :String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                fileImageUser = uri
                binding.userImg.setImageURI(uri)
            } else {
                Log.d("PhotoPicker", "No media selected")
                Toast.makeText(activity, "No media selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSignupBinding.bind(view)
        onClicks()
        observers()

    }


    private fun onClicks() {

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSignup.setOnClickListener() {
            binding.progress.visibility = View.VISIBLE
            signupFragmentViewModel.validate(
                binding.editTxtFullName.text.toString(),
                binding.editTxtUsername.text.toString(),
                binding.editTxtEmail.text.toString(),
                binding.editTxtPassword.text.toString(),
                binding.editTxtConfirmPassword.text.toString(),
                fileImageUser,
                binding.editTxtBirthDate.text.toString(),
                userAge
            )
        }
        binding.userImg.setOnClickListener {
            openPhotoPicker()
        }

        binding.btnBirthdate.setOnClickListener {
            val myCalendar = Calendar.getInstance()
            val datePicker = DatePickerDialog.OnDateSetListener { datePicker, year, month, dayOfMonth ->
                myCalendar.set(Calendar.YEAR,year)
                myCalendar.set(Calendar.MONTH,month)
                myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                updateLabel(myCalendar)
            }
            DatePickerDialog(requireContext(),
                datePicker,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun updateLabel(myCalendar: Calendar) {
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat,Locale.UK)
        binding.editTxtBirthDate.setText(sdf.format(myCalendar.time))
        calculateAge(sdf.format(myCalendar.time))
    }

    private fun calculateAge(birthDate: String) {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        // Get the current date
        val currentDate = Calendar.getInstance()
        // Parse the birth date string to a Date object
        val parsedBirthDate = dateFormat.parse(birthDate)
        // Set the birth date in the Calendar instance
        val calendarBirthDate = Calendar.getInstance()
        calendarBirthDate.time = parsedBirthDate!!
        // Calculate the age
        var age: Int = currentDate.get(Calendar.YEAR) - calendarBirthDate.get(Calendar.YEAR)
        // Adjust the age if the birth date hasn't occurred yet this year
        if (currentDate.get(Calendar.DAY_OF_YEAR) < calendarBirthDate.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        userAge = age.toString()
    }

    private fun openPhotoPicker() {
        val pickVisualMediaRequest = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        pickMedia.launch(pickVisualMediaRequest)
    }


        private fun observers() {
            signupFragmentViewModel.validateResult.observe(viewLifecycleOwner) {

                if (it.equals(VAL_IMG_PROB)) {
                    binding.progress.visibility = View.GONE
                    Toast.makeText(activity, "Please select profile picture", Toast.LENGTH_SHORT)
                        .show()
                } else if (it.equals(VAL_NAME_PROB)) {
                    binding.progress.visibility = View.GONE
                    binding.textInputLayoutFullName.error = "Required"
                } else if (it.equals(VAL_USERNAME_PROB)) {
                    binding.progress.visibility = View.GONE
                    binding.textInputLayoutUserName.error = "Required"
                } else if (it.equals(VAL_EMAIL_PROB)) {
                    binding.progress.visibility = View.GONE
                    binding.textInputLayoutEmail.error = "Required"
                } else if (it.equals(VAL_PASS_PROB)) {
                    binding.progress.visibility = View.GONE
                    binding.textInputLayoutPassword.error = "Required"
                } else if (it.equals(VAL_CONFPASS_PROB)) {
                    binding.progress.visibility = View.GONE
                    binding.textInputLayoutConfirmPassword.error = "Doesn't match password"
                }else if (it.equals(VAL_DATE_PROB)){
                    binding.progress.visibility = View.GONE
                    binding.textInputLayoutBirthdate.error = "Required"
                }else if (it.equals(VAL_NO_PROB)) {
                    binding.progress.visibility = View.GONE
                    val intent = Intent(requireActivity(), HomeActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }else{
                    binding.progress.visibility = View.GONE
                    Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
                }
            }


        }
}
 */