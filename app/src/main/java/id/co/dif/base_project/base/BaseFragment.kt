package id.co.dif.base_project.base

import android.content.Context
import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import id.co.dif.base_project.data.BasicInfo
import id.co.dif.base_project.data.Session
import id.co.dif.base_project.persistence.Preferences
import id.co.dif.base_project.utils.WeakContextDelegate
import java.lang.reflect.ParameterizedType


/***
 * Created by kikiprayudi
 * on Monday, 27/02/23 15:36
 *
 */

abstract class BaseFragment<VM : BaseViewModel, VB : ViewBinding> : Fragment() {


    var fragmentContext by WeakContextDelegate<Context>()
    lateinit var currentActivity: BaseActivity<*, *>
    lateinit var preferences: Preferences
    lateinit var lifecycleOwner: LifecycleOwner
    var locationRequestPermanentlyDeclined: Boolean = false

    lateinit var viewModel: VM
    lateinit var binding: VB

    var session: Session? = null
        get() = viewModel.session
    var basiInfo: BasicInfo? = null
        get() = viewModel.basiInfo
    abstract val layoutResId: Int

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.cancelAllJobs()
    }
    override fun onDetach() {
        super.onDetach()
        fragmentContext = null
    }

    override fun onPause() {
        super.onPause()
        viewModel.cancelAllJobs()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        currentActivity = requireActivity() as BaseActivity<*, *>
        preferences = currentActivity.preferences
        lifecycleOwner = viewLifecycleOwner
        binding = DataBindingUtil.inflate(layoutInflater, layoutResId, container, false)
        viewModel = ViewModelProvider.NewInstanceFactory().create(getViewModelClass())
        return binding.root
    }

    fun showToast(message: String){
        currentActivity.showToast(message)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.uiState.observe(lifecycleOwner) {
            currentActivity.viewModel.uiState.postValue(it)
        }
        onViewBindingCreated(savedInstanceState)
    }





    @Suppress("UNCHECKED_CAST")
    fun getViewModelClass(): Class<VM> {
        return (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<VM>
    }

    abstract fun onViewBindingCreated(savedInstanceState: Bundle?)


    open fun refresh() {}

}