package ru.kgu.taskcalendar

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // 1. Получаем базу и DAO
        val database = AppDatabase.getDatabase(this)
        val taskDao = database.taskDao()

        // 2. Создаём адаптер с пустым списком (данные придут из базы)
        val adapter = TaskAdapter(emptyList()) { task ->
            val intent = Intent(this, TaskDetailActivity::class.java)
            intent.putExtra("TASK_ID", task.id)
            startActivity(intent)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // 3. Подписываемся на Flow из базы и добавляем тестовые данные при первом запуске
        lifecycleScope.launch {
            // Проверяем, был ли уже первый запуск
            val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
            val isFirstLaunch = prefs.getBoolean("is_first_launch", true)

            if (isFirstLaunch) {
                // Добавляем тестовые задачи
                taskDao.insert(
                    Task(
                        title = "Сделать уроки",
                        description = "русский, математика, физика",
                        date = "25.09.2026"
                    )
                )
                taskDao.insert(
                    Task(
                        title = "Погулять с собакой",
                        description = "поводок в шкафу",
                        date = "25.09.2026"
                    )
                )
                taskDao.insert(
                    Task(
                        title = "Сходить в магазин",
                        description = "купить хлеб, молоко",
                        date = "26.09.2026"
                    )
                )
                taskDao.insert(
                    Task(
                        title = "Убраться",
                        description = "помыть посуду, пропылесосить",
                        date = "27.09.2026"
                    )
                )



                // Ставим флаг, что первый запуск прошёл
                prefs.edit().putBoolean("is_first_launch", false).apply()
            }


            // Подписываемся на поток задач из базы
            taskDao.getAllTasks().collect { tasks ->
                adapter.submitList(tasks)
            }
        }

        // 4. FAB — переход на добавление
        val fab = findViewById<FloatingActionButton>(R.id.fabAdd)
        fab.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }

        // 5. Обработка отступов — оставляем как было
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}