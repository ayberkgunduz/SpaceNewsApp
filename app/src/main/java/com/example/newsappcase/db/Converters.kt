package com.example.newsappcase.db

import androidx.room.TypeConverter
import com.example.newsappcase.model.Event
import com.example.newsappcase.model.Launche
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun fromEventsList(events: List<Event>?): String? {
        return Gson().toJson(events)
    }

    @TypeConverter
    fun toEventsList(eventsString: String?): List<Event>? {
        val listType = object : TypeToken<List<Event>>() {}.type
        return Gson().fromJson(eventsString, listType)
    }

    @TypeConverter
    fun fromLaunchesList(launches: List<Launche>?): String? {
        return Gson().toJson(launches)
    }

    @TypeConverter
    fun toLaunchesList(launchesString: String?): List<Launche>? {
        val listType = object : TypeToken<List<Launche>>() {}.type
        return Gson().fromJson(launchesString, listType)
    }
}