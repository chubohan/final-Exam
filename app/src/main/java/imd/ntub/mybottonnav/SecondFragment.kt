package imd.ntub.mybottonnav

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.core.os.bundleOf

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SecondFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var user: User? = null
    private var position: Int = -1
    private lateinit var userRepository: UserRepository

    private var editMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            user = it.getParcelable("user")
            position = it.getInt("position", -1)
            editMode = it.getBoolean("editMode", false)
        }
        userRepository = UserRepository(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val edtName = view.findViewById<EditText>(R.id.edtName)
        val edtPhone = view.findViewById<EditText>(R.id.edtPhone)

        user?.let {
            edtName.setText(it.name)
            edtPhone.setText(it.phone)
        }

        btnSave.setOnClickListener {
            val name = edtName.text.toString()
            val phone = edtPhone.text.toString()

            if (editMode) {
                val updatedUser = user!!.copy(name = name, phone = phone)
                userRepository.updateUser(updatedUser)
                parentFragmentManager.setFragmentResult("editUserResultKey", bundleOf(
                    "user" to updatedUser,
                    "position" to position
                ))
            } else {
                val newUser = User(0, name, phone)
                val id = userRepository.addUser(newUser).toInt()
                parentFragmentManager.setFragmentResult("addUserRequestKey", bundleOf("user" to newUser.copy(id = id)))
            }

            parentFragmentManager.popBackStack()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String, user: User?, position: Int) =
            SecondFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putParcelable("user", user)
                    putInt("position", position)
                    putBoolean("editMode", position != -1)
                }
            }
    }
}
