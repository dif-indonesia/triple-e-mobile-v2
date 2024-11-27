package id.co.dif.base_project.utils

import android.widget.EditText
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout

/***
 * Created by kikiprayudi
 * on Wednesday, 01/03/23 10:08
 *
 */


fun TextInputLayout.valueIsEmpty(): Boolean {
    if (this.editText?.text?.toString().isNullOrEmpty()) {
        if (this.editText?.hint.isNullOrEmpty()) {
            Toast.makeText(this.context, "Field harus diisi", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this.context, "${this.editText?.hint} harus diisi", Toast.LENGTH_SHORT).show()
        }
        return true
    }
    return false
}

fun EditText.valueIsEmpty(): Boolean {
    if (this.text?.toString().isNullOrEmpty()) {
        if (this.hint.isNullOrEmpty()) {
            Toast.makeText(this.context, "Field harus diisi", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this.context, "${this.hint} harus diisi", Toast.LENGTH_SHORT).show()
        }
        return true
    }
    return false
}

fun TextInputLayout.valueIsDifferent(tilConfirmation: TextInputLayout): Boolean {
    if (this.editText?.text.toString() != tilConfirmation.editText?.text.toString()) {
        tilConfirmation.editText?.hint?.let {
//                this.context.toast("$it harus sama")
        }
        return true
    }
    return false
}

fun TextInputLayout.minLength(length: Int): Boolean {
    if ((this.editText?.text?.length ?: 0) < length) {
        if (this.editText?.hint.isNullOrEmpty()) {
//                this.context.toast("Field minimal ${length} karakter")
        } else {
//                this.context.toast("${this.editText?.hint} minimal ${length} karakter")
        }
        return true
    }
    return false
}