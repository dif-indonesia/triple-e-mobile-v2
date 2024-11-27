package id.co.dif.base_project.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

/***
 * Created by kikiprayudi
 * on Thursday, 02/03/23 22:11
 *
 */


abstract class BaseDialog<VM : BaseViewModel, VB : ViewBinding> : DialogFragment() {

    lateinit var viewModel: VM
    lateinit var binding: VB

    abstract val layoutResId: Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
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
        onViewBindingCreated(savedInstanceState)
    }

    abstract fun onViewBindingCreated(savedInstanceState: Bundle?)
}