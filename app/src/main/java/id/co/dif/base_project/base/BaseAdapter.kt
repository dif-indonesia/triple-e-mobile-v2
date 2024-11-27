package id.co.dif.base_project.base

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import id.co.dif.base_project.data.BaseData
import id.co.dif.base_project.persistence.Preferences
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.lang.reflect.ParameterizedType

/***
 * Created by kikiprayudi
 * on Tuesday, 14/03/23 16:04
 *
 */


abstract class BaseAdapter<VM : BaseViewModel, VB : ViewBinding, T : BaseData> :
    RecyclerView.Adapter<BaseViewHolder<VB>>(), KoinComponent {


    lateinit var viewModel: VM
    val preferences: Preferences by inject()

    abstract val layoutResId: Int

    var data = mutableListOf<T>()

    var context: Context? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<VB> {
        context = parent.context
        val binding: VB = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            layoutResId,
            parent,
            false
        )

        viewModel = ViewModelProvider.NewInstanceFactory().create(getViewModelClass())
        return BaseViewHolder(binding)
    }

    @Suppress("UNCHECKED_CAST")
    fun getViewModelClass(): Class<VM> {
        return (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<VM>
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<VB>, position: Int) {
        onLoadItem(holder.binding, data, position)
    }

    abstract fun onLoadItem(binding: VB, data: MutableList<T>, position: Int)
}