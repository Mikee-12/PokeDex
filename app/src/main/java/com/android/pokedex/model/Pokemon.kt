package com.android.pokedex.model

import com.google.gson.annotations.SerializedName

data class Pokemon(
    val id: Int,
    val name: String,
    val sprites: Sprites,
    val species: Species? = null,
    val height: Int? = null,
    val weight: Int? = null,
    val types: List<TypeSlot>? = null,
    val abilities: List<AbilitySlot>? = null,
    val stats: List<StatSlot>? = null
)

data class Sprites(
    @SerializedName("front_default")
    val front_default: String?,
    @SerializedName("back_default")
    val back_default: String? = null,
    @SerializedName("front_shiny")
    val front_shiny: String? = null,
    val other: OtherSprites? = null,
    val versions: Map<String, Any>? = null
)

data class OtherSprites(
    @SerializedName("official-artwork")
    val officialArtwork: OfficialArtwork? = null,
    val home: HomeSprites? = null,
    @SerializedName("dream_world")
    val dreamWorld: DreamWorldSprites? = null
)

data class OfficialArtwork(
    @SerializedName("front_default")
    val front_default: String?,
    @SerializedName("front_shiny")
    val front_shiny: String?
)

data class HomeSprites(
    @SerializedName("front_default")
    val front_default: String?
)

data class DreamWorldSprites(
    @SerializedName("front_default")
    val front_default: String?
)

data class Species(
    val name: String,
    val url: String
)

data class TypeSlot(
    val slot: Int,
    val type: Type
)

data class Type(
    val name: String,
    val url: String
)

data class AbilitySlot(
    val ability: Ability,
    @SerializedName("is_hidden")
    val isHidden: Boolean,
    val slot: Int
)

data class Ability(
    val name: String,
    val url: String
)

data class StatSlot(
    @SerializedName("base_stat")
    val baseStat: Int,
    val effort: Int,
    val stat: Stat
)

data class Stat(
    val name: String,
    val url: String
)