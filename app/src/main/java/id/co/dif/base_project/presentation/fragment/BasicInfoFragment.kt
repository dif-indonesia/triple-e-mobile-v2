package id.co.dif.base_project.presentation.fragment

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseFragment
import id.co.dif.base_project.databinding.BasicInfoBinding
import id.co.dif.base_project.utils.orDefault
import id.co.dif.base_project.viewmodel.BasicInfoViewModel
import java.lang.Exception
import java.time.LocalDate
import java.time.Period

class BasicInfoFragment : BaseFragment<BasicInfoViewModel, BasicInfoBinding>() {
    override val layoutResId = R.layout.basic_info

    //    private fun calculateAge(birthdate: Date): Int {
//        val now = Calendar.getInstance()
//        val dob = Calendar.getInstance()
//        dob.time = birthdate
//
//        var age = now.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
//        if (now.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
//            age--
//        }
//
//        return age
//    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getAge(birthdate: LocalDate): Int {
        val today = LocalDate.now()
        val period = Period.between(birthdate, today)
        return period.years
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        var info = preferences.myDetailProfile.value
        val url = info?.linkedin.toString()
        binding.linkedin.setOnClickListener {
            openLink(url)
        }

        binding.linkedin.underline()

        viewModel.responseDetailedProfile.observe(lifecycleOwner) {
            if (it.status == 200) {
                var info = preferences.myDetailProfile.value

                binding.fullname.setText(it.data.fullname.orDefault())
                binding.position.setText(it.data.position.orDefault())
                binding.location.setText(it.data.location.orDefault())
                binding.phone.setText(it.data.phone.orDefault())
                binding.email.setText(it.data.email.orDefault())
                binding.gender.setText(it.data.gender.orDefault())
                binding.joint.setText(it.data.joint.orDefault())
                binding.nik.setText(it.data.nik.orDefault())
                binding.bpjs.setText(it.data.bpjs.orDefault())
                binding.age.setText("${it.data.age} years")
                binding.linkedin.setText(it.data.linkedin.orDefault())

                if (it.data.id != info?.id){
                    binding.lineJoint.visibility = View.GONE
                    binding.lineNik.visibility = View.GONE
                    binding.lineBpjs.visibility = View.GONE
                    binding.lineAge.visibility = View.GONE
                    binding.lineLinkedin.visibility = View.GONE
                } else
                {

                }

            }
        }
        val id = preferences.selectedProfileId.value
        viewModel.getDetailProfile(id)
    }

    private fun openLink(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            showToast("Url appears to be invalid!")
        } catch (e: Exception) {
            showToast("Something went wrong!")
        }
    }

    fun TextView.underline() {
        paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }

    override fun onResume() {
        super.onResume()
        val id = preferences.selectedProfileId.value
        viewModel.getDetailProfile(id)
    }

}