package com.example.learningcoroutines

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.learningcoroutines.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.buttonLoad.setOnClickListener {
            //таким способом асинхронный код будет иметь жизненный цикл, т.е запрос отменится если активити умрет, поэтому корутина запускается внутри lifecycleScope
            lifecycleScope.launch {
                loadData()
            }
        }
    }
    //Использование корутин это программирование с колбэками, они не видны, их создает компилятор
    //Suspend функции не должны блокировать поток
    //Suspend функции под капотом используют State машину чтобы один и тот же метод можно было
    // вызывать с разными состояниями
    //Suspend функции можно запустить только из других Suspend функций, или из корутин скоупа
    private suspend fun loadData() {
        binding.progress.isVisible = true
        binding.buttonLoad.isEnabled = false
        val city = loadCity()
        binding.tvLocation.text = city
        val temperature = loadTemperature(city)
        binding.tvTemperature.text = temperature.toString()
        binding.progress.isVisible = false
        binding.buttonLoad.isEnabled = true
    }

    private suspend fun loadCity(): String {
        delay(5000) //при использовании suspend функций, функция приостановит свое
        //выполнение на 5 секунд, таким образом главный поток не будет заблокирован на это время.
      // По истечении заданного времени функция вернется к выполнению с следующей строки
        return "Moscow"
    }

    private suspend fun loadTemperature(city: String): Int {
        Toast.makeText(
            this,
            getString(R.string.loading_temperature_toast, city),
            Toast.LENGTH_SHORT
        ).show()
        delay(5000)
        return 17
    }
}