


import android.content.Intent
import android.os.Bundle
import android.widget.CalendarView
import androidx.appcompat.app.AppCompatActivity
import com.example.mobilecalendar.databinding.ActivityMainBinding
import com.example.mobilecalendar.databinding.TodoBinding



class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.calendar.setOnClickListener{
            val intent = Intent(this , CalendarView::class.java)
            startActivity(intent)
        }
        binding.todo.setOnClickListener {
            val intent = Intent(this, TodoBinding::class.java)
            startActivity(intent)
        }
    }
}
