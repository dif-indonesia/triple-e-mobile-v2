package id.co.dif.base_project.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.co.dif.base_project.persistence.Preferences
import java.lang.reflect.ParameterizedType

/***
 * Created by kikiprayudi
 * on Thursday, 02/03/23 22:11
 *
 */


abstract class BaseBottomSheetDialog<VM : BaseViewModel, VB : ViewBinding, T> : BottomSheetDialogFragment() {

    lateinit var currentActivity: BaseActivity<*, *>
    lateinit var lifecycleOwner: LifecycleOwner
    lateinit var viewModel: VM
    lateinit var binding: VB
    lateinit var preferences: Preferences

    abstract val layoutResId: Int

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener {
                val bottomSheet = findViewById<FrameLayout>(
                    com.google.android.material.R.id.design_bottom_sheet
                )
                bottomSheet.setBackgroundResource(android.R.color.transparent)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        currentActivity = requireActivity() as BaseActivity<*, *>
        preferences = currentActivity.preferences
        lifecycleOwner = viewLifecycleOwner
        binding = DataBindingUtil.inflate(layoutInflater, layoutResId, container, false)
        viewModel = ViewModelProvider.NewInstanceFactory().create(getViewModelClass())
        return binding.root
    }

    @Suppress("UNCHECKED_CAST")
    fun getViewModelClass(): Class<VM> {
        return (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<VM>
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.uiState.observe(lifecycleOwner) {
            currentActivity.viewModel.uiState.postValue(it)
        }
        onViewBindingCreated(savedInstanceState)
    }

    abstract fun onViewBindingCreated(savedInstanceState: Bundle?)
}