package imd.ntub.mybottonnav

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// 接口在此定义
interface OnItemClickListener {
    fun onItemClick(user: User, position: Int)
}

class FirstFragment : Fragment() {
    private lateinit var userRepository: UserRepository
    private lateinit var users: ArrayList<User>
    private lateinit var myAdapter: MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userRepository = UserRepository(requireContext())
        users = ArrayList(userRepository.getAllUsers())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myAdapter = MyAdapter(users, object : OnItemClickListener {
            override fun onItemClick(user: User, position: Int) {
                showEditDialog(user, position)
            }
        })

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = myAdapter
    }

    private fun showEditDialog(user: User, position: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_user, null)
        val edtName = dialogView.findViewById<EditText>(R.id.edtName)
        val edtPhone = dialogView.findViewById<EditText>(R.id.edtPhone)

        // 初始化对话框中的数据
        edtName.setText(user.name)
        edtPhone.setText(user.phone)

        AlertDialog.Builder(requireContext())
            .setTitle("編輯聯絡人")
            .setView(dialogView)
            .setPositiveButton("保存") { _, _ ->
                val newName = edtName.text.toString()
                val newPhone = edtPhone.text.toString()

                if (newName.isNotEmpty() && newPhone.isNotEmpty()) {
                    users[position] = user.copy(name = newName, phone = newPhone)
                    myAdapter.notifyItemChanged(position)
                    userRepository.updateUser(users[position])
                } else {
                    Toast.makeText(requireContext(), "姓名和電話不能為空", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    fun addUser(user: User) {
        users.add(user)
        myAdapter.notifyItemInserted(users.size - 1)
    }

    fun updateUser(user: User, position: Int) {
        if (position != -1 && position < users.size) {
            users[position] = user
            myAdapter.notifyItemChanged(position)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() = FirstFragment()
    }

    inner class MyAdapter(private val data: ArrayList<User>, private val listener: OnItemClickListener) :
        RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imageView: ImageView = view.findViewById(R.id.imageView)
            val txtName: TextView = view.findViewById(R.id.txtName)
            val txtPhone: TextView = view.findViewById(R.id.txtPhone)
            val imageButton: ImageButton = view.findViewById(R.id.imageButton)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_row, parent, false))
        }

        override fun getItemCount() = data.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val currentUser = data[position]
            holder.txtName.text = currentUser.name
            holder.txtPhone.text = currentUser.phone

            holder.itemView.setOnClickListener {
                listener.onItemClick(currentUser, position)
            }

            holder.imageView.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:" + currentUser.phone)
                it.context.startActivity(intent)
            }

            holder.imageButton.setOnClickListener {
                AlertDialog.Builder(it.context)
                    .setMessage("確定刪除此聯絡人？")
                    .setPositiveButton("確定") { _, _ ->
                        data.removeAt(position)
                        notifyItemRemoved(position)
                    }
                    .setNegativeButton("取消", null)
                    .show()
            }
        }
    }
}
