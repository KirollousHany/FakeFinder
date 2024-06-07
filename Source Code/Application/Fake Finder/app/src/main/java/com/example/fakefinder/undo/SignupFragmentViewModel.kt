/*package com.example.fakefinder.ui.Auth.signup
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fakefinder.Models.ModelUser
import com.example.fakefinder.USERS
import com.example.fakefinder.VAL_CONFPASS_PROB
import com.example.fakefinder.VAL_DATE_PROB
import com.example.fakefinder.VAL_EMAIL_PROB
import com.example.fakefinder.VAL_IMG_PROB
import com.example.fakefinder.VAL_NAME_PROB
import com.example.fakefinder.VAL_NO_PROB
import com.example.fakefinder.VAL_PASS_PROB
import com.example.fakefinder.VAL_USERNAME_PROB
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignupFragmentViewModel @Inject constructor(private val databaseReference: DatabaseReference,
    private val auth : FirebaseAuth,private val storageReference: StorageReference) : ViewModel(){
    private val _validateResult = MutableLiveData<String>()
    val validateResult get() = _validateResult

    fun validate(fullName: String, username: String, email: String, password: String, confirmPassword: String,
                 fileImageUser: Uri?,birthDate : String,age : String?) {

        if (fileImageUser == null) {
            _validateResult.value = VAL_IMG_PROB
        } else if (fullName.isNullOrEmpty()) {
            _validateResult.value = VAL_NAME_PROB
        } else if (username.isNullOrEmpty()) {
            _validateResult.value = VAL_USERNAME_PROB
        } else if (email.isNullOrEmpty()) {
            _validateResult.value = VAL_EMAIL_PROB
        } else if (password.isNullOrEmpty()) {
            _validateResult.value = VAL_PASS_PROB
        } else if (confirmPassword != password) {
            _validateResult.value = VAL_CONFPASS_PROB
        }else if (birthDate.isNullOrEmpty()){
            _validateResult.value = VAL_DATE_PROB
        }else{
            signup(fullName, username, email, password,birthDate,age!!,fileImageUser)
        }
    }

    private fun signup(fullName: String, username: String, email: String, password: String,
                       birthDate: String,age: String, image : Uri){
        auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {
            setImageToStorage(fullName, username, email, image,birthDate,age)
        }.addOnFailureListener(){
            _validateResult.value = it.localizedMessage
        }
    }

    private fun setImageToStorage (fullName: String,username: String,email: String,
                                   image : Uri,birthDate: String,age: String){
        storageReference.child("ProfileImages/")
            .child(auth.uid.toString())
            .putFile(image).addOnSuccessListener {
                storageReference.child("ProfileImages/")
                    .child(auth.uid.toString()).downloadUrl.addOnSuccessListener {
                        sendDataToRealTime(fullName,username,email, it.toString(),birthDate,age)
                    }
            }.addOnFailureListener {
                _validateResult.value = it.localizedMessage
            }
    }

    private fun sendDataToRealTime(fullName: String,username: String,email: String,
                                   imageUrl : String,birthDate: String,age: String){
        databaseReference.child(USERS)
            .child(auth.uid.toString())
            .setValue(ModelUser(imageUrl,fullName,username,email,auth.uid.toString(),age))
            .addOnSuccessListener {
                _validateResult.value = VAL_NO_PROB
            }.addOnFailureListener {
                _validateResult.value = it.localizedMessage
            }
    }


}
 */