package com.android.pokedex

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.ui.res.stringResource
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import com.android.pokedex.model.Pokemon
import com.android.pokedex.ui.theme.PokedexTheme
import com.android.pokedex.utils.loadPokemon

class PokemonDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ambil ID pokemon dari intent
        val pokemonId = intent.getIntExtra("POKEMON_ID", 1)
        val pokemon = loadPokemon(this, pokemonId)

        setContent {
            PokedexTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (pokemon != null) {
                        PokemonDetailScreen(
                            pokemon = pokemon,
                            onBackClick = { finish() }
                        )
                    } else {
                        // Error state
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Pokemon tidak ditemukan")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PokemonDetailScreen(
    pokemon: Pokemon,
    onBackClick: () -> Unit
) {
    val imageUrl = pokemon.sprites.other?.officialArtwork?.front_default
        ?: pokemon.sprites.front_default

    // Deteksi dark mode
    val isDarkMode = isSystemInDarkTheme()

    // Deteksi orientasi landscape
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Bagian atas - Header (Nomor & Nama) - mengikuti dark mode
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 64.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Nomor Pokemon
                Text(
                    text = "#${pokemon.id.toString().padStart(4, '0')}",
                    fontSize = if (isLandscape) 16.sp else 20.sp,
                    color = if (isDarkMode) Color.LightGray else Color.Gray,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(if (isLandscape) 4.dp else 8.dp))

                // Nama Pokemon
                Text(
                    text = pokemon.name.replaceFirstChar { it.uppercase() },
                    fontSize = if (isLandscape) 24.sp else 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            // Box putih untuk konten - SELALU PUTIH bahkan di dark mode
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                color = Color.White, // Selalu putih
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    if (isLandscape) {
                        // Layout Landscape: Gambar di kiri, Info di kanan
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Kolom Kiri: Gambar Pokemon
                            Column(
                                modifier = Modifier.weight(0.45f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Card Gambar Pokemon (lebih besar)
                                Card(
                                    modifier = Modifier.size(280.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFF343539)
                                    )
                                ) {
                                    SubcomposeAsyncImage(
                                        model = imageUrl,
                                        contentDescription = pokemon.name,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        loading = {
                                            Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                CircularProgressIndicator()
                                            }
                                        },
                                        error = {
                                            Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("?", fontSize = 64.sp, color = Color.Gray)
                                            }
                                        }
                                    )
                                }
                            }

                            // Kolom Kanan: Info (Type, Tinggi, Berat, Ability)
                            Column(
                                modifier = Modifier.weight(0.55f),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Types
                                pokemon.types?.let { types ->
                                    InfoSectionCompact(title = stringResource(id = R.string.type)) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            types.forEach { typeSlot ->
                                                TypeChipCompact(typeName = typeSlot.type.name)
                                            }
                                        }
                                    }
                                }

                                // Height & Weight
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    InfoCardCompact(
                                        label = "Tinggi",
                                        value = "${(pokemon.height ?: 0) / 10.0} m",
                                        modifier = Modifier.weight(1f)
                                    )
                                    InfoCardCompact(
                                        label = "Berat",
                                        value = "${(pokemon.weight ?: 0) / 10.0} kg",
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                // Abilities
                                pokemon.abilities?.let { abilities ->
                                    InfoSectionCompact(title = stringResource(id = R.string.abylity)) {
                                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            abilities.forEach { abilitySlot ->
                                                AbilityItemCompact(
                                                    name = abilitySlot.ability.name,
                                                    isHidden = abilitySlot.isHidden
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats di bawah (full width)
                        pokemon.stats?.let { stats ->
                            InfoSection(title = stringResource(id = R.string.stats)) {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    stats.forEach { statSlot ->
                                        StatBar(
                                            statName = statSlot.stat.name,
                                            statValue = statSlot.baseStat
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Layout Portrait (Original)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Card Gambar Pokemon
                            Card(
                                modifier = Modifier.size(250.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF343539)
                                )
                            ) {
                                SubcomposeAsyncImage(
                                    model = imageUrl,
                                    contentDescription = pokemon.name,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    loading = {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    },
                                    error = {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("?", fontSize = 64.sp, color = Color.Gray)
                                        }
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Types
                            pokemon.types?.let { types ->
                                InfoSection(title = stringResource(id = R.string.type),) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        types.forEach { typeSlot ->
                                            TypeChip(typeName = typeSlot.type.name)
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            // Height & Weight
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                InfoCard(
                                    label = "Tinggi",
                                    value = "${(pokemon.height ?: 0) / 10.0} m",
                                    modifier = Modifier.weight(1f)
                                )
                                InfoCard(
                                    label = "Berat",
                                    value = "${(pokemon.weight ?: 0) / 10.0} kg",
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Abilities
                            pokemon.abilities?.let { abilities ->
                                InfoSection(title = stringResource(id = R.string.abylity),) {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        abilities.forEach { abilitySlot ->
                                            AbilityItem(
                                                name = abilitySlot.ability.name,
                                                isHidden = abilitySlot.isHidden
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            // Stats
                            pokemon.stats?.let { stats ->
                                InfoSection(title = stringResource(id = R.string.stats),) {
                                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        stats.forEach { statSlot ->
                                            StatBar(
                                                statName = statSlot.stat.name,
                                                statValue = statSlot.baseStat
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Back button (floating)
        Box(
            modifier = Modifier
                .padding(16.dp)
                .size(48.dp)
                .clip(CircleShape)
                .background(if (isDarkMode) Color(0xFF2D2D2D) else Color.White)
                .clickable(onClick = onBackClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = if (isDarkMode) Color.White else Color.Black
            )
        }
    }
}

@Composable
fun InfoSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF343539)
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun InfoCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF343539)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun TypeChip(typeName: String) {
    val typeColor = getTypeColor(typeName)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = typeColor,
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            text = typeName.replaceFirstChar { it.uppercase() },
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun AbilityItem(name: String, isHidden: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFF4A4C50),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name.replace("-", " ").replaceFirstChar { it.uppercase() },
            modifier = Modifier.weight(1f),
            fontSize = 15.sp,
            color = Color.White
        )
        if (isHidden) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFFFB74D)
            ) {
                Text(
                    text = "Hidden",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 12.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun StatBar(statName: String, statValue: Int) {
    val maxStat = 255
    val progress = (statValue.toFloat() / maxStat).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFF4A4C50),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when (statName) {
                    "hp" -> "HP"
                    "attack" -> "Attack"
                    "defense" -> "Defense"
                    "special-attack" -> "Sp. Attack"
                    "special-defense" -> "Sp. Defense"
                    "speed" -> "Speed"
                    else -> statName.replace("-", " ").replaceFirstChar { it.uppercase() }
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Text(
                text = statValue.toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = getStatColor(statValue)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF2A2C30))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .clip(RoundedCornerShape(4.dp))
                    .background(getStatColor(statValue))
            )
        }
    }
}

// Helper function untuk warna type
fun getTypeColor(typeName: String): Color {
    return when (typeName.lowercase()) {
        "normal" -> Color(0xFFA8A878)
        "fire" -> Color(0xFFF08030)
        "water" -> Color(0xFF6890F0)
        "electric" -> Color(0xFFF8D030)
        "grass" -> Color(0xFF78C850)
        "ice" -> Color(0xFF98D8D8)
        "fighting" -> Color(0xFFC03028)
        "poison" -> Color(0xFFA040A0)
        "ground" -> Color(0xFFE0C068)
        "flying" -> Color(0xFFA890F0)
        "psychic" -> Color(0xFFF85888)
        "bug" -> Color(0xFFA8B820)
        "rock" -> Color(0xFFB8A038)
        "ghost" -> Color(0xFF705898)
        "dragon" -> Color(0xFF7038F8)
        "dark" -> Color(0xFF705848)
        "steel" -> Color(0xFFB8B8D0)
        "fairy" -> Color(0xFFEE99AC)
        else -> Color.Gray
    }
}

// Helper function untuk warna stat
fun getStatColor(value: Int): Color {
    return when {
        value >= 150 -> Color(0xFF4CAF50) // Green
        value >= 100 -> Color(0xFF8BC34A) // Light Green
        value >= 70 -> Color(0xFFFFEB3B) // Yellow
        value >= 50 -> Color(0xFFFF9800) // Orange
        else -> Color(0xFFF44336) // Red
    }
}

// Compact versions for landscape mode
@Composable
fun InfoSectionCompact(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF343539)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun InfoCardCompact(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF343539)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun TypeChipCompact(typeName: String) {
    val typeColor = getTypeColor(typeName)

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = typeColor,
        modifier = Modifier.padding(2.dp)
    ) {
        Text(
            text = typeName.replaceFirstChar { it.uppercase() },
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

@Composable
fun AbilityItemCompact(name: String, isHidden: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFF4A4C50),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name.replace("-", " ").replaceFirstChar { it.uppercase() },
            modifier = Modifier.weight(1f),
            fontSize = 13.sp,
            color = Color.White
        )
        if (isHidden) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFFFFB74D)
            ) {
                Text(
                    text = "Hidden",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    fontSize = 10.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}