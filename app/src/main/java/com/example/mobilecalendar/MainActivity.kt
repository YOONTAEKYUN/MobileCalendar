


import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import com.example.mobilecalendar.TodoMainActivity
import com.example.mobilecalendar.databinding.ActivityMainBinding

import com.example.mobilecalendar.view.CalendarViewContainer
import com.example.mobilecalendar.view.EditTodoActivity
import com.example.mobilecalendar.view.TodoViewModel


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.calendar.setOnClickListener{
            val intent = Intent(this , CalendarViewContainer::class.java)
            startActivity(intent)
        }
        binding.todo.setOnClickListener {
            val intent = Intent(this, TodoMainActivity::class.java)
            startActivity(intent)
        }
    }
}
