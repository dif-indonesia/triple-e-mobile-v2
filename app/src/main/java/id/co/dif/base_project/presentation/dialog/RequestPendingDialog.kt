package id.co.dif.base_project.presentation.dialog

import android.app.AlertDialog
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import br.com.ilhasoft.support.validation.Validator
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseBottomSheetDialog
import id.co.dif.base_project.data.Employee
import id.co.dif.base_project.databinding.FragmentPopupRequestPendingBinding
import id.co.dif.base_project.utils.isNotNullOrEmpty
import id.co.dif.base_project.utils.log
import id.co.dif.base_project.utils.trimAllEditText
import id.co.dif.base_project.viewmodel.InboxViewModel
import id.co.dif.base_project.viewmodel.PermitViewModel

class RequestPendingDialog (
    var id: Int? = null,
    var ticketNumber: String? = null,
    var reasonPending: String? = null,
    var pndInformationText: String? = null,
    var emp_pic_id: Int? = null,
    var pndCanceled: Boolean? = null,
    var pndApproved: Boolean? = null,
    private val onActionComplete: (() -> Unit)? = null // Callback untuk refresh

) : BaseBottomSheetDialog<PermitViewModel, FragmentPopupRequestPendingBinding, Any>() {

    override val layoutResId = R.layout.fragment_popup_request_pending

    companion object {
        fun newInstance(onSuccess: (String, String, Int) -> Unit) = PopupMessagesDialog(onSuccess)
    }

    private lateinit var rootView: View

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        super.onCreateDialog(savedInstanceState)

        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        val myProfile = preferences.myDetailProfile.value
        binding.titleTicket.text = "Request Pending Ticket# ${ticketNumber}"

        if (id == null || pndCanceled == true){
            binding.tvApproveInformation.isVisible = false
            binding.pndInformation.isVisible = false
            binding.btnSend.isVisible = true
            binding.btnDecline.isVisible = false
            binding.btnApprove.isVisible = false
        }

        if (reasonPending.isNotNullOrEmpty() && pndCanceled != true) {
            binding.reason.setText(reasonPending)
            binding.reason.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.gray)
            )
            binding.reason.isClickable = false
            binding.reason.isEnabled = false
        }

        if (pndInformationText.isNotNullOrEmpty() && pndCanceled != true){
            binding.pndInformation.setText(pndInformationText)
            binding.pndInformation.isClickable = false
            binding.pndInformation.isEnabled = false
            binding.btnApprove.isVisible = false
            binding.btnDecline.isVisible = false
        }

        if (id != null && pndInformationText.isNullOrEmpty() && pndCanceled != true && emp_pic_id != myProfile?.id){
            binding.tvApproveInformation.isVisible = false
            binding.pndInformation.isVisible = false
            binding.btnSend.isVisible = false
            binding.btnCancel.isVisible = true
            binding.btnDecline.isVisible = false
            binding.btnApprove.isVisible = false
            binding.btnSend.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.gray)
            )
        }

        if (pndApproved == true && pndCanceled != true && emp_pic_id != myProfile?.id){
            binding.btnCancel.isVisible = true
        }

        binding.btnHide.setOnClickListener {
            dismiss()
        }



        binding.reason.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // No need to implement this
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No need to implement this
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length ?: 0 >= 20) {
                    binding.reason.requestFocus()
                }
            }
        })

        binding.btnSend.setOnClickListener {
            if (binding.reason.text.isNullOrEmpty()){
                Toast.makeText(context, "Reason is required!", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.requestPending(
                    id = ticketNumber,
                    mutableMapOf(
                        "reason" to binding.reason.text.toString(),
                    )
                )
            }
        }

        binding.btnCancel.setOnClickListener {
                viewModel.cancelPending(
                    id = ticketNumber
                )
        }

        binding.btnApprove.setOnClickListener {
            if (binding.reason.text.isNullOrEmpty()){
                Toast.makeText(context, "Approve Information is required!", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.approvePending(
                    id = id,
                    mutableMapOf(
                        "pnd_approved" to true,
                        "pnd_information" to binding.pndInformation.text.toString(),
                    )
                )
            }
        }

        binding.btnDecline.setOnClickListener {
            if (binding.reason.text.isNullOrEmpty()){
                Toast.makeText(context, "Approve Information is required!", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.approvePending(
                    id = id,
                    mutableMapOf(
                        "pnd_approved" to false,
                        "pnd_information" to binding.pndInformation.text.toString(),
                    )
                )
            }
        }

        viewModel.responseRequestPending.observe(lifecycleOwner) {
            if (it.status == 200) {
                Toast.makeText(context, "Succusfully submit!", Toast.LENGTH_SHORT).show()
                dismiss()
                onActionComplete?.invoke()
            } else {
                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show()
            }
        }




    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }

    fun createDialog(
        context: Context?, layout: Int, animation: Int, showSoftInput: Boolean,
        gravity: Int
    ): AlertDialog? {
        val dialog = AlertDialog.Builder(context).create()
        dialog.show() /*from   w w  w  .  j a  v  a  2 s .  c o m*/
        dialog.setContentView(layout)
        val window = dialog.window
        if (showSoftInput) {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
            window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
        val lp = window!!.attributes
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = gravity
        window.attributes = lp
        window.setWindowAnimations(animation)
        return dialog
    }


}