package com.example.mobilecalendar

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobilecalendar.adapter.TodoAdapter
import com.example.mobilecalendar.databinding.TodoBinding
import com.example.mobilecalendar.dto.Todo
import com.example.mobilecalendar.repository.TodoRepository
import com.example.mobilecalendar.view.EditTodoActivity
import com.example.mobilecalendar.view.TodoViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TodoMainActivity : Fragment() {
    private lateinit var binding: TodoBinding
    private lateinit var todoViewModel: TodoViewModel
    private lateinit var todoAdapter: TodoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TodoBinding.inflate(inflater, container, false)
        // TodoRepository 초기화
        TodoRepository.initialize(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        binding.fabAdd.setOnClickListener {
            val intent = Intent(requireContext(), EditTodoActivity::class.java).apply {
                putExtra("type", "ADD")
            }
            requestActivity.launch(intent)
        }

        todoViewModel = ViewModelProvider(this).get(TodoViewModel::class.java)
        todoViewModel.todoList.observe(viewLifecycleOwner) { todoList ->
            todoAdapter.update(todoList)
        }


        todoAdapter = TodoAdapter(requireContext())
        binding.rvTodoList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTodoList.adapter = todoAdapter

        todoAdapter.setItemCheckBoxClickListener(object : TodoAdapter.ItemCheckBoxClickListener {
            override fun onClick(view: View, position: Int, itemId: Long) {
                //Toast.makeText(requireContext(), "$itemId", Toast.LENGTH_SHORT).show()
                CoroutineScope(Dispatchers.IO).launch {
                    val todo = todoViewModel.getOne(itemId)
                    todo.isChecked = !todo.isChecked
//                    // 아이템의 상태 변경 후 RecyclerView에 변경사항을 알리기 위해 notifyItemChanged 호출
//                    withContext(Dispatchers.Main) {
//                        // UI 업데이트는 메인 스레드에서 수행
//                        todoAdapter.notifyItemChanged(position)
//                    }
                }
            }
        })

        todoAdapter.setItemClickListener(object : TodoAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, itemId: Long) {
                CoroutineScope(Dispatchers.IO).launch {
                    // 클릭 이벤트 처리
                    val todo = todoViewModel.getOne(itemId)
                    val intent = Intent(requireContext(), EditTodoActivity::class.java).apply {
                        putExtra("type", "EDIT")
                        putExtra("item", todo)
                    }
                    requestActivity.launch(intent)
                    todoViewModel.update(todo)
                }

            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_option, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.menu_item_delete -> {
                Toast.makeText(requireContext(), "삭제", Toast.LENGTH_SHORT).show()
                todoViewModel.todoList.value!!.forEach {
                    if (it.isChecked) {
                        todoViewModel.delete(it)
                    }
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val requestActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val todo = it.data?.getSerializableExtra("todo") as Todo

            when (it.data?.getIntExtra("flag", -1)) {
                0 -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        todoViewModel.insert(todo)
                    }
                    Toast.makeText(requireContext(), "추가되었습니다.", Toast.LENGTH_SHORT).show()
                }
                1 -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        todoViewModel.update(todo)
                    }
                    Toast.makeText(requireContext(), "수정되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
