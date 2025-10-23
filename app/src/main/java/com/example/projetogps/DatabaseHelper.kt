package com.example.projetogps

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "clima.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE = "CREATE TABLE clima (id INTEGER PRIMARY KEY AUTOINCREMENT, cidade TEXT, temperatura TEXT, descricao TEXT, umidade TEXT, velocidade_vento TEXT, data_hora TEXT)"
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS clima")
        onCreate(db)
    }


    fun insertWeather(weather: Weather): Long {
        val db = this.writableDatabase
        
        val values = ContentValues().apply {
            put("cidade", weather.cidade)
            put("temperatura", weather.temperatura)
            put("descricao", weather.descricao)
            put("umidade", weather.umidade)
            put("velocidade_vento", weather.velocidadeVento)
            put("data_hora", weather.dataHora)
        }
        
        val status = db.insert("clima", null, values)
        db.close()
        return status
    }


    fun deleteWeather(id: Int): Int {
        val db = this.writableDatabase
        val success = db.delete("clima", "id=" + id, null)
        db.close()
        return success
    }


    fun selectAllWeather(): ArrayList<Weather> {
        val weatherList = ArrayList<Weather>()
        val selectQuery = "SELECT * FROM clima ORDER BY id DESC"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        
        if (cursor.moveToFirst()) {
            do {
                val weather = Weather(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cidade = cursor.getString(cursor.getColumnIndexOrThrow("cidade")),
                    temperatura = cursor.getString(cursor.getColumnIndexOrThrow("temperatura")),
                    descricao = cursor.getString(cursor.getColumnIndexOrThrow("descricao")),
                    umidade = cursor.getString(cursor.getColumnIndexOrThrow("umidade")),
                    velocidadeVento = cursor.getString(cursor.getColumnIndexOrThrow("velocidade_vento")),
                    dataHora = cursor.getString(cursor.getColumnIndexOrThrow("data_hora"))
                )
                weatherList.add(weather)
            } while (cursor.moveToNext())
        }
        
        cursor.close()
        db.close()
        return weatherList
    }


    fun deleteAllWeather(): Int {
        val db = this.writableDatabase
        val success = db.delete("clima", null, null)
        db.close()
        return success
    }
}