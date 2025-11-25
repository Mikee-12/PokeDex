package com.android.pokedex.utils

import android.content.Context
import com.android.pokedex.model.Pokemon
import com.android.pokedex.model.PokemonType
import com.google.gson.Gson

fun loadPokemon(context: Context, id: Int): Pokemon {
    // BACA FILE JSON DARI assets/pokemon/1.json
    val jsonText = context.assets.open("pokemon/$id.json")
        .bufferedReader()
        .use { it.readText() }

    // PARSE KE DATA CLASS POKEMON
    return Gson().fromJson(jsonText, Pokemon::class.java)
}

fun loadType(context: Context, typeName: String): PokemonType {
    // BACA FILE JSON DARI assets/type/grass.json
    val jsonText = context.assets.open("type/${typeName.lowercase()}.json")
        .bufferedReader()
        .use { it.readText() }

    // PARSE KE DATA CLASS POKEMONTYPE
    return Gson().fromJson(jsonText, PokemonType::class.java)
}

fun getAllTypeNames(): List<String> {
    return listOf(
        "normal", "fire", "water", "electric", "grass", "ice",
        "fighting", "poison", "ground", "flying", "psychic", "bug",
        "rock", "ghost", "dragon", "dark", "steel", "fairy"
    )
}