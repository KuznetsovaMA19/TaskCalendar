package ru.kgu.taskcalendar

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    // 1. Подключаем ViewModel. by viewModels() сам создаст её и сохранит при повороте.
    private val viewModel: TaskViewModel by this.viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // 2. Настраиваем адаптер и RecyclerView (как и раньше)
        val adapter = TaskAdapter(emptyList()) { task ->
            val intent = Intent(this, TaskDetailActivity::class.java)
            intent.putExtra("TASK_ID", task.id)
            startActivity(intent)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // 3. Подписываемся на данные из ViewModel (а не напрямую из базы!)
        lifecycleScope.launch {
            viewModel.allTasks.collect { tasks ->
                adapter.submitList(tasks)
            }
        }

        // 4. Проверяем первый запуск и добавляем тестовые задачи ЧЕРЕЗ ViewModel
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isFirstLaunch = prefs.getBoolean("is_first_launch", true)
        if (isFirstLaunch) {
            viewModel.addTask("Сделать уроки", "русский, математика, физика", "25.09.2026")
            viewModel.addTask("Погулять с собакой", "поводок в шкафу", "25.09.2026")
            viewModel.addTask("Сходить в магазин", "купить хлеб, молоко", "26.09.2026")
            viewModel.addTask("Убраться", "помыть посуду, пропылесосить", "27.09.2026")
            prefs.edit().putBoolean("is_first_launch", false).apply()
        }

        // 5. FAB
        val fab = findViewById<FloatingActionButton>(R.id.fabAdd)
        fab.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }

        // 6. Отступы
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}