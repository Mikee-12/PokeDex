package com.android.pokedex.model

import com.google.gson.annotations.SerializedName

data class PokemonType(
    val id: Int,
    val name: String,
    @SerializedName("damage_relations")
    val damageRelations: DamageRelations? = null,
    val pokemon: List<TypePokemon>? = null
)

data class DamageRelations(
    @SerializedName("double_damage_from")
    val doubleDamageFrom: List<TypeReference>? = null,
    @SerializedName("double_damage_to")
    val doubleDamageTo: List<TypeReference>? = null,
    @SerializedName("half_damage_from")
    val halfDamageFrom: List<TypeReference>? = null,
    @SerializedName("half_damage_to")
    val halfDamageTo: List<TypeReference>? = null,
    @SerializedName("no_damage_from")
    val noDamageFrom: List<TypeReference>? = null,
    @SerializedName("no_damage_to")
    val noDamageTo: List<TypeReference>? = null
)

data class TypeReference(
    val name: String,
    val url: String
)

data class TypePokemon(
    val pokemon: PokemonReference,
    val slot: Int
)

data class PokemonReference(
    val name: String,
    val url: String
)