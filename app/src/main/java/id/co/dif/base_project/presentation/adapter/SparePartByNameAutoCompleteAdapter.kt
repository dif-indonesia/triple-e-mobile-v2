package id.co.dif.base_project.presentation.adapter

import android.content.Context
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes
import id.co.dif.base_project.data.SparePart

class SparePartByNameAutoCompleteAdapter(
    context: Context,
    @LayoutRes layout: Int,
    list: List<SparePart>
) : ArrayAdapter<SparePart>(context, layout, list) {

}