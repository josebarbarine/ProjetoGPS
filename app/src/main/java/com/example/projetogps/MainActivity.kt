package com.example.projetogps;


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import com.example.projetogps.R


class MainActivity : AppCompatActivity() {

    private lateinit var etCidade: EditText
    private lateinit var btnBuscar: Button
    private lateinit var btnSalvar: Button
    private lateinit var btnHistorico: Button
    private lateinit var btnLimpar: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutResultado: LinearLayout
    private lateinit var imgWeatherMain: ImageView
    private lateinit var txtCidadeMain: TextView
    private lateinit var txtTemperaturaMain: TextView
    private lateinit var txtDescricaoMain: TextView
    private lateinit var txtUmidadeMain: TextView
    private lateinit var txtVentoMain: TextView
    private lateinit var recyclerView: RecyclerView

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var weatherAdapter: WeatherAdapter
    private var currentWeather: Weather? = null

    private val API_KEY = "e3271200"
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkInternetPermission()

        initializeViews()
        setupDatabase()
        setupListeners()
    }

    private fun initializeViews() {
        etCidade = findViewById(R.id.etCidade)
        btnBuscar = findViewById(R.id.btnBuscar)
        btnSalvar = findViewById(R.id.btnSalvar)
        btnHistorico = findViewById(R.id.btnHistorico)
        btnLimpar = findViewById(R.id.btnLimpar)
        progressBar = findViewById(R.id.progressBar)
        layoutResultado = findViewById(R.id.layoutResultado)
        imgWeatherMain = findViewById(R.id.imgWeatherMain)
        txtCidadeMain = findViewById(R.id.txtCidadeMain)
        txtTemperaturaMain = findViewById(R.id.txtTemperaturaMain)
        txtDescricaoMain = findViewById(R.id.txtDescricaoMain)
        txtUmidadeMain = findViewById(R.id.txtUmidadeMain)
        txtVentoMain = findViewById(R.id.txtVentoMain)
        recyclerView = findViewById(R.id.recyclerView)

        btnSalvar.isEnabled = false
        recyclerView.visibility = View.GONE
        layoutResultado.visibility = View.GONE
        progressBar.visibility = View.GONE
    }

    private fun setupDatabase() {
        databaseHelper = DatabaseHelper(this)

        weatherAdapter = WeatherAdapter(ArrayList()) { weather ->
            deleteWeatherRecord(weather)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = weatherAdapter
    }

    private fun setupListeners() {
        btnBuscar.setOnClickListener {
            val cidade = etCidade.text.toString().trim()
            if (cidade.isNotEmpty()) {
                buscarClima(cidade)
            } else {
                Toast.makeText(this, "Digite o nome de uma cidade", Toast.LENGTH_SHORT).show()
            }
        }

        btnSalvar.setOnClickListener {
            salvarClima()
        }

        btnHistorico.setOnClickListener {
            mostrarHistorico()
        }

        btnLimpar.setOnClickListener {
            limparHistorico()
        }
    }

    private fun buscarClima(cidade: String) {
        progressBar.visibility = View.VISIBLE
        layoutResultado.visibility = View.GONE
        recyclerView.visibility = View.GONE
        btnSalvar.isEnabled = false

        val url = "https://api.hgbrasil.com/weather?key=$API_KEY&city_name=$cidade&format=json-cors"
        Log.d(TAG, "Buscando clima na URL: $url")

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                Log.d(TAG, "Resposta da API recebida: $response")
                progressBar.visibility = View.GONE
                processarResposta(response)
            },
            { error ->
                progressBar.visibility = View.GONE
                Log.e(TAG, "Erro Volley: ${error.message}", error)
                val networkResponse = error.networkResponse
                if (networkResponse != null) {
                    Log.e(TAG, "Status Code Volley: ${networkResponse.statusCode}")
                    try {
                        val responseBody = String(networkResponse.data, Charsets.UTF_8)
                        Log.e(TAG, "Corpo da resposta Volley: $responseBody")
                    } catch (e: Exception) {
                        Log.e(TAG, "Erro ao ler corpo da resposta Volley", e)
                    }
                }
                Toast.makeText(this, "Erro ao buscar dados. Verifique a cidade ou a conexão.", Toast.LENGTH_LONG).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun processarResposta(response: JSONObject) {
        try {
            if (response.has("error") && response.getBoolean("error")) {
                val errorMessage = response.optString("message", "Erro retornado pela API.")
                Log.e(TAG, "Erro da API HG Brasil: $errorMessage")
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                return
            }

            if (!response.has("results")) {
                Log.e(TAG, "Resposta JSON não contém o objeto 'results'.")
                Toast.makeText(this, "Formato de resposta inesperado da API.", Toast.LENGTH_SHORT).show()
                return
            }

            val results = response.getJSONObject("results")

            val cidade = results.optString("city", "Cidade não encontrada")
            val temperatura = results.optInt("temp", 0).toString()
            val descricao = results.optString("description", "-")
            val umidade = results.optInt("humidity", 0).toString()
            val velocidadeVento = results.optString("wind_speedy", "-")

            if (cidade.isEmpty() || cidade == "Cidade não encontrada") {
                Toast.makeText(this, "Cidade não encontrada ou dados inválidos.", Toast.LENGTH_LONG).show()
                return
            }


            val dataHora = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

            currentWeather = Weather(
                cidade = cidade,
                temperatura = temperatura,
                descricao = descricao,
                umidade = umidade,
                velocidadeVento = velocidadeVento,
                dataHora = dataHora
            )

            exibirResultado()

        } catch (e: JSONException) {
            Log.e(TAG, "Erro ao processar JSON: ${e.message}", e)
            Toast.makeText(this, "Erro ao processar dados recebidos.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(TAG, "Erro inesperado ao processar resposta: ${e.message}", e)
            Toast.makeText(this, "Erro inesperado.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun exibirResultado() {
        currentWeather?.let { weather ->
            txtCidadeMain.text = weather.cidade
            txtTemperaturaMain.text = "${weather.temperatura}°C"
            txtDescricaoMain.text = weather.descricao
            txtUmidadeMain.text = "Umidade: ${weather.umidade}%"
            txtVentoMain.text = "Vento: ${weather.velocidadeVento}"

            val lowerDesc = weather.descricao.lowercase(Locale.getDefault())
            when {
                lowerDesc.contains("chuva") || lowerDesc.contains("chuvoso") || lowerDesc.contains("tempestade") ->
                    imgWeatherMain.setImageResource(R.drawable.ic_rain)
                lowerDesc.contains("nublado") || lowerDesc.contains("nuvens") ->
                    imgWeatherMain.setImageResource(R.drawable.ic_cloud)
                else ->
                    imgWeatherMain.setImageResource(R.drawable.ic_sun)
            }

            layoutResultado.visibility = View.VISIBLE
            btnSalvar.isEnabled = true
        } ?: run {
            Log.e(TAG, "Tentativa de exibir resultado com currentWeather nulo.")
            layoutResultado.visibility = View.GONE
            btnSalvar.isEnabled = false
        }
    }

    private fun salvarClima() {
        currentWeather?.let { weather ->
            val status = databaseHelper.insertWeather(weather)

            if (status > -1) {
                Toast.makeText(this, "Salvo no histórico", Toast.LENGTH_SHORT).show()
                btnSalvar.isEnabled = false
            } else {
                Toast.makeText(this, "Erro ao salvar no banco de dados", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Erro ao inserir no banco de dados, status: $status")
            }
        } ?: Toast.makeText(this, "Nenhum dado de clima para salvar", Toast.LENGTH_SHORT).show()
    }

    private fun mostrarHistorico() {
        val weatherList = databaseHelper.selectAllWeather()

        if (weatherList.isEmpty()) {
            recyclerView.visibility = View.GONE
            Toast.makeText(this, "Histórico vazio", Toast.LENGTH_SHORT).show()
        } else {
            recyclerView.visibility = View.VISIBLE
            weatherAdapter.updateList(weatherList)
            layoutResultado.visibility = View.GONE
        }
    }

    private fun deleteWeatherRecord(weather: Weather) {
        val status = databaseHelper.deleteWeather(weather.id)

        if (status > 0) {
            Toast.makeText(this, "Registro removido", Toast.LENGTH_SHORT).show()
            mostrarHistorico()
        } else {
            Toast.makeText(this, "Erro ao remover registro", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "Erro ao deletar do banco de dados, ID: ${weather.id}, status: $status")
        }
    }

    private fun limparHistorico() {
        val status = databaseHelper.deleteAllWeather()

        if (status > -1) {
            Toast.makeText(this, "Histórico limpo", Toast.LENGTH_SHORT).show()
            weatherAdapter.updateList(ArrayList())
            recyclerView.visibility = View.GONE
        } else {
            Toast.makeText(this, "Erro ao limpar histórico", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "Erro ao deletar tudo do banco de dados, status: $status")
        }
    }

    private fun checkInternetPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
            != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Permissão de INTERNET não encontrada no Manifest!")
            Toast.makeText(this, "Permissão de Internet ausente!", Toast.LENGTH_LONG).show()

        }
    }

}