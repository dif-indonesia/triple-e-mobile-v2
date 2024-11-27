package id.co.dif.base_project.presentation.activity

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.databinding.ActivityOnBoarddingBinding
import id.co.dif.base_project.presentation.fragment.DialogLoginFragment
import id.co.dif.base_project.utils.stopLocationServiceScheduler
import timber.log.Timber

class OnBoardingActivity : BaseActivity<BaseViewModel, ActivityOnBoarddingBinding>() {
    override val layoutResId = R.layout.activity_on_boardding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate onboarding: ")
        stopLocationServiceScheduler()
    }
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        binding.btnGetStarted.setOnClickListener {
            showDialog()
        }
    }
    private fun showDialog() {
        val dialog: DialogFragment = DialogLoginFragment()
        dialog.show(supportFragmentManager, "dialog"    )
    }

    override fun onBackPressed() {
        val alertDialog = AlertDialog.Builder(this)
            .setMessage(getString(R.string.are_you_sure_you_want_to_exit))
            .setPositiveButton("Yes") { _, _ ->
                finishAffinity()
            }
            .setNegativeButton("Cancel", null)
            .create()

        alertDialog.show()
    }

    override fun onResume() {
        super.onResume()
    }
}