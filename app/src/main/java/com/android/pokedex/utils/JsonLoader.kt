package com.android.pokedex.utils

import android.content.Context
import com.android.pokedex.model.Pokemon
import com.google.gson.Gson

fun loadPokemon(context: Context, id: Int): Pokemon {

    // BACA FILE JSON DARI assets/pokemon/1.json
    val jsonText = context.assets.open("pokemon/$id.json")
        .bufferedReader()
        .use { it.readText() }

    // PARSE KE DATA CLASS POKEMON
    return Gson().fromJson(jsonText, Pokemon::class.java)
}
