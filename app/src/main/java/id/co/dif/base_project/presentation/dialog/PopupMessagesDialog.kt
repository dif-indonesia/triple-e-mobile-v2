package id.co.dif.base_project.presentation.dialog

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import br.com.ilhasoft.support.validation.Validator
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseBottomSheetDialog
import id.co.dif.base_project.data.Employee
import id.co.dif.base_project.databinding.FragmentPopupMessagesBinding
import id.co.dif.base_project.utils.trimAllEditText
import id.co.dif.base_project.viewmodel.InboxViewModel


class PopupMessagesDialog(var onSuccess: (message: String, receiver: String, messageId: Int) -> Unit) :
    BaseBottomSheetDialog<InboxViewModel, FragmentPopupMessagesBinding, Any>() {

    override val layoutResId = R.layout.fragment_popup_messages

    companion object {
        fun newInstance(onSuccess: (String, String, Int) -> Unit) = PopupMessagesDialog(onSuccess)
    }

    private lateinit var rootView: View

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        super.onCreateDialog(savedInstanceState)

        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        val dialogBuilder = AlertDialog.Builder(
            requireContext(),
            android.R.style.Theme_Material_Dialog_Presentation
        )

        binding.btnHide.setOnClickListener {
            dismiss()
        }

        binding.receiver.setOnClickListener {
            dialogBuilder
        }

        binding.messages.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // No need to implement this
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No need to implement this
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length ?: 0 >= 20) {
                    binding.messages.requestFocus()
                }
            }
        })


        viewModel.responseListName.observe(lifecycleOwner) {
            if (it.status == 200 && it.data.list.isNotEmpty()) {
                val adapter: ArrayAdapter<Employee> = ArrayAdapter<Employee>(
                    requireContext(),
                    R.layout.item_spinner_dropdown,
                    it.data.list
                )
                binding.receiver.setAdapter(adapter)
                if (binding.receiver.text.isNotEmpty() && adapter.count != 1) {
                    binding.receiver.showDropDown()
                }
            }
        }
        binding.receiver.doOnTextChanged { text, start, before, count ->
            if (binding.receiver.hasFocus()) {
                viewModel.getListName(requireContext(), text.toString())
            }
        }


        binding.btnSend.setOnClickListener {

            binding.messages.setText(binding.messages.text.trim())
            val message = binding.messages.text.toString()
            val truncatedMessage = message.substring(0, message.length.coerceAtMost(50))
            currentActivity.trimAllEditText(binding.root)
            val validator = Validator(binding)
            validator.enableFormValidationMode();
            if (validator.validate().not()) return@setOnClickListener

            viewModel.sendMessages(
                mutableMapOf(
                    "chat_receiver" to binding.receiver.text.toString(),
                    "chat_content" to binding.messages.text.toString(),
                )
            )

//            viewModel.sendMessages(
//                mutableMapOf(
//                    "mes_receiver" to binding.receiver.text.toString(),
//                    "mes_class" to truncatedMessage,
//                    "mes_reff_id" to 0,
//                    "mes_content" to binding.messages.text.toString(),
//                    "mes_show" to 0
//                )
//            )
        }


        viewModel.responseSendMessages.observe(lifecycleOwner) {
//            runBlocking {
            if (it.status == 200) {
                dismiss()
                Toast.makeText(requireContext(), "Messages send succesfully", Toast.LENGTH_SHORT)
                    .show()
                onSuccess(
                    binding.messages.text.toString(),
                    binding.receiver.text.toString(),
                    it.data.chat_id
                )
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