package com.example.pethaven.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.pethaven.R


/*
    From George Andreas MyRuns
 */
/**
 * Dialog Fragment supporting Edit Text
 */
class EditTextDialog : DialogFragment(), DialogInterface.OnClickListener {
    private lateinit var editText: EditText
    private var editListener: OnEditClickListener? = null

    companion object {
        private const val TITLE_BUNDLE = "Title key for Bundle"
        private const val INPUT_TYPE_BUNDLE = "Input Type key for Bundle"
        private const val TEXT_BUNDLE = "Text key for Bundle"
        private const val HINT_BUNDLE = "Hint key for Bundle"

        //Static method to pass arguments to this fragment
        fun createInstance(title: String, inputType: Int, text: String? = null, hint: String? = null) : EditTextDialog {
            val editDialog = EditTextDialog()
            val args = Bundle()

            args.putString(TITLE_BUNDLE, title)
            args.putInt(INPUT_TYPE_BUNDLE, inputType)
            args.putString(TEXT_BUNDLE, text)
            args.putString(HINT_BUNDLE, hint)
            editDialog.arguments = args

            return editDialog
        }
    }

    interface OnEditClickListener{
        fun onEditClick(dialog: EditTextDialog, item: Int, text: String)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        lateinit var dialog : Dialog

        //Get necessary settings for the dialog
        val title = arguments?.getString(TITLE_BUNDLE, "")
        val inputType = arguments?.getInt(INPUT_TYPE_BUNDLE, InputType.TYPE_CLASS_TEXT)
        val text = arguments?.getString(TEXT_BUNDLE, "")
        val hint = arguments?.getString(HINT_BUNDLE, "")

        //Create view and set EditText settings
        val view = requireActivity().layoutInflater.inflate(R.layout.edit_text_dialog, null)
        editText = view.findViewById(R.id.editTextDialog)
        editText.inputType = inputType ?: InputType.TYPE_CLASS_TEXT
        editText.setText(text)
        editText.hint = hint

        //Create dialog builder
        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(view)
        builder.setTitle(title)
        builder.setPositiveButton("Ok", this)
        builder.setNegativeButton("Cancel", this)
        dialog = builder.create()

        return dialog
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        editListener = requireActivity() as? OnEditClickListener
        println("debug: editListener is ${if (editListener == null) "null" else "not null"}")
    }

    override fun onClick(dialog: DialogInterface?, item: Int) {
        editListener?.onEditClick(this, item, editText.text.toString())
    }

}