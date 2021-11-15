package com.example.learningcoroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ProxyFileDescriptorCallback
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.learningcoroutines.databinding.ActivityMainBinding
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
//работать с view элементами можно только на главном потоке. Чтобы из других потоков вызвать
//какой то код на главном потоке, используется класс Handler
//Handler ипользует очередь сообщений из класса Looper. При создании обьекта Handler ему нужно
// передать Looper в качестве параметра Handler(Looper.getMainLooper()) если события нужно
// обрабатывать на главном потоке
//Если события нужно обрабатывать не на главном потоке то перед созданием обьекта Handler нужно
// вызвать метод Looper.prepare() после чего в Handler в качестве параметра передать Handler(Looper.myLooper())


    //для того чтобы разные потоки могли общаться и передавать друг другу данные, существует Handler
    //обьект этого класса можно создать на главном потоке, затем из любого потока ему можно передавать
    //обьекты Runnable, и тогда метод Run будет вызван на главном потоке
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.buttonLoad.setOnClickListener {
            loadData()
        }
    }

    private fun loadData() {
        binding.progress.isVisible = true
        binding.buttonLoad.isEnabled = false
        loadCity { it ->
            binding.tvLocation.text = it
            loadTemperature(it){
                binding.tvTemperature.text = it.toString()
                binding.progress.isVisible = false
                binding.buttonLoad.isEnabled = true
            }

        }
    }

    private fun loadCity(callback:(String) -> Unit){
        thread {//создается новый поток
            Thread.sleep(5000) //поток засыпает на 5 секунд, после будет вызван метод колбэка
            // на главном потоке, т.к хэндлер был создан на нем
            handler.post {  //метод post вызовет метод run мгновенно, а если необходимо установить
  // задержку перед вызовом, нужно вызвать postDelayed в котором указать задержку в микросекундах
                callback.invoke("Moscow")
            }
        }
    }

    private fun loadTemperature(city: String, callback: (Int) -> Unit){
        thread {
    //можно использовать такой вызов, "запусти на главном потоке", под капотом используется тот же хэндлер
            runOnUiThread{
                Toast.makeText(
                    this,
                    getString(R.string.loading_temperature_toast, city),
                    Toast.LENGTH_SHORT
                ).show()
            }
            Thread.sleep(5000)
            runOnUiThread {
                callback.invoke(17)
            }
        }

    }
}