package com.example.mobilecalendar

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobilecalendar.adapter.TodoAdapter
import com.example.mobilecalendar.databinding.TodoBinding
import com.example.mobilecalendar.dto.Todo
import com.example.mobilecalendar.view.EditTodoActivity
import com.example.mobilecalendar.view.TodoViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TodoMainActivity : AppCompatActivity() {
    lateinit var binding: TodoBinding
    lateinit var todoViewModel: TodoViewModel
    lateinit var todoAdapter: TodoAdapter

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_option, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId) {
            R.id.menu_item_delete -> {
                Toast.makeText(this, "삭제", Toast.LENGTH_SHORT).show()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TodoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, EditTodoActivity::class.java).apply {
                putExtra("type", "ADD")
            }
            requestActivity.launch(intent)
        }
        todoViewModel = ViewModelProvider(this)[TodoViewModel::class.java]
        todoViewModel.todoList.observe(this) {
            todoAdapter.update(it)
        }

        todoAdapter = TodoAdapter(this)
        binding.rvTodoList.layoutManager = LinearLayoutManager(this)
        binding.rvTodoList.adapter = todoAdapter

        todoAdapter.setItemCheckBoxClickListener(object: TodoAdapter.ItemCheckBoxClickListener {
            override fun onClick(view: View, position: Int, itemId: Long) {
                Toast.makeText(this@TodoMainActivity, "$itemId", Toast.LENGTH_SHORT).show()
                CoroutineScope(Dispatchers.IO).launch {
                    val todo = todoViewModel.getOne(itemId)

                    val intent = Intent(this@TodoMainActivity, EditTodoActivity::class.java).apply {
                        putExtra("type", "EDIT")
                        putExtra("item", todo)
                    }
                    requestActivity.launch(intent)
                    todo.isChecked = !todo.isChecked
                    todoViewModel.update(todo)
                }
            }
        })
    }



    private val requestActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val todo = it.data?.getSerializableExtra("todo") as Todo

                when (it.data?.getIntExtra("flag", -1)) {
                    0 -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            todoViewModel.insert(todo)
                        }
                        Toast.makeText(this, "추가되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                    1 -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            todoViewModel.update(todo)
                        }
                        Toast.makeText(this, "수정되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
    }