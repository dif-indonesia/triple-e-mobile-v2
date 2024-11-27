package id.co.dif.base_project.presentation.dialog

import android.os.Bundle
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseBottomSheetDialog
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.Note
import id.co.dif.base_project.databinding.DialogNotesPopupBinding
import id.co.dif.base_project.presentation.adapter.TimeLineAdapter

/***
 * Created by kikiprayudi
 * on Wednesday, 01/03/23 04:28
 *
 */


class NotePopupDialog(val notes: List<Note>) :
    BaseBottomSheetDialog<BaseViewModel, DialogNotesPopupBinding, Any?>() {

    override val layoutResId = R.layout.dialog_notes_popup
    companion object {
        fun newInstance(notes: List<Note>) = NotePopupDialog(notes)
    }

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        binding.rvTimeline.adapter = TimeLineAdapter().also {
            it.data.addAll(notes)
            it.notifyItemRangeChanged(0, notes.size)
            it.viewingOnly = true
        }
    }
}