package com.example.fakefinder.ui.Auth.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.fakefinder.R
import com.example.fakefinder.utils.VAL_EMAIL_PROB
import com.example.fakefinder.utils.VAL_NO_PROB
import com.example.fakefinder.utils.VAL_PASS_PROB
import com.example.fakefinder.databinding.FragmentLoginBinding
import com.example.fakefinder.ui.Activities.HomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val loginFragmentViewModel: LoginFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)
        onClicks()
        observers()
    }

    private fun onClicks() {
        binding.txtForgetPass.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToForgetPassFragment())
        }
        binding.btnCreateAcc.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToNameGenderFragment())
        }
        binding.btnLogin.setOnClickListener {
            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val windowToken: IBinder? = requireActivity().currentFocus?.windowToken

            if (windowToken != null) {
                imm.hideSoftInputFromWindow(windowToken, 0)
            }
            binding.btnLogin.text = ""
            binding.progress.visibility = View.VISIBLE
            loginFragmentViewModel.validate(
                binding.editTxtEmail.text.toString().trim(),
                binding.editTxtPass.text.toString().trim()
            )
        }
        binding.editTxtEmail.doAfterTextChanged {
            binding.textInputLayout.error = ""
        }
        binding.editTxtPass.doAfterTextChanged {
            binding.textInputLayout1.error = ""
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observers() {
        loginFragmentViewModel.validateResult.observe(viewLifecycleOwner) {
            if (it.equals(VAL_EMAIL_PROB)) {
                binding.btnLogin.text = "Login"
                binding.progress.visibility = View.GONE
                binding.textInputLayout.error = "Required"
            } else if (it.equals(VAL_PASS_PROB)) {
                binding.btnLogin.text = "Login"
                binding.progress.visibility = View.GONE
                binding.textInputLayout1.error = "Required"
            } else if (it.equals(VAL_NO_PROB)) {
                val intent = Intent(activity, HomeActivity::class.java)
                startActivity(intent)
                activity?.finish()
            } else {
                binding.btnLogin.text = "Login"
                binding.progress.visibility = View.GONE
                Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}