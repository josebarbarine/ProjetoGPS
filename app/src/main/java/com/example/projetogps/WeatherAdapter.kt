package com.example.projetogps

import com.example.projetogps.R

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WeatherAdapter : RecyclerView.Adapter<WeatherAdapter.ViewHolder> {

    private val weatherList: ArrayList<Weather>
    private val onDeleteClick: (Weather) -> Unit

    constructor(weatherList: ArrayList<Weather>, onDeleteClick: (Weather) -> Unit) : super() {
        this.weatherList = weatherList
        this.onDeleteClick = onDeleteClick
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgWeather: ImageView = view.findViewById(R.id.imgWeather)
        val txtCidade: TextView = view.findViewById(R.id.txtCidade)
        val txtTemperatura: TextView = view.findViewById(R.id.txtTemperatura)
        val txtDescricao: TextView = view.findViewById(R.id.txtDescricao)
        val txtDataHora: TextView = view.findViewById(R.id.txtDataHora)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.weather_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weather = weatherList[position]
        
        holder.txtCidade.text = weather.cidade
        holder.txtTemperatura.text = "${weather.temperatura}°C"
        holder.txtDescricao.text = weather.descricao
        holder.txtDataHora.text = weather.dataHora
        
        when {
            weather.descricao.contains("chuv", ignoreCase = true) -> 
                holder.imgWeather.setImageResource(R.drawable.ic_rain)
            weather.descricao.contains("nublado", ignoreCase = true) -> 
                holder.imgWeather.setImageResource(R.drawable.ic_cloud)
            else -> 
                holder.imgWeather.setImageResource(R.drawable.ic_sun)
        }
        
        holder.btnDelete.setOnClickListener {
            onDeleteClick(weather)
        }
    }

    override fun getItemCount(): Int = weatherList.size
    
    fun updateList(newList: ArrayList<Weather>) {
        weatherList.clear()
        weatherList.addAll(newList)
        notifyDataSetChanged()
    }
}

