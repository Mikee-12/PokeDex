package com.android.pokedex

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import com.android.pokedex.model.Pokemon
import com.android.pokedex.ui.theme.PokedexTheme
import com.android.pokedex.utils.loadPokemon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PokemonListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PokedexTheme {
                PokemonListScreen()
            }
        }
    }
}

// Data class untuk range pokemon
data class PokemonRange(val label: String, val start: Int, val end: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var pokemonList by remember { mutableStateOf<List<Pokemon>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    // Generate daftar range pokemon (1-60, 61-120, ..., 961-1020, 1021-1025)
    val pokemonRanges = remember {
        buildList {
            var start = 1
            while (start <= 1025) {
                val end = minOf(start + 59, 1025)
                add(PokemonRange("$start-$end", start, end))
                start += 60
            }
        }
    }

    var selectedRange by remember { mutableStateOf(pokemonRanges[0]) }
    var expandedDropdown by remember { mutableStateOf(false) }

    val context = androidx.compose.ui.platform.LocalContext.current

    // Load pokemon saat range berubah
    LaunchedEffect(selectedRange) {
        isLoading = true
        pokemonList = loadPokemonBatch(context, selectedRange.start, selectedRange.end)
        isLoading = false
    }

    // Filter pokemon berdasarkan search query
    val filteredPokemon = remember(searchQuery, pokemonList) {
        if (searchQuery.isBlank()) {
            pokemonList
        } else {
            pokemonList.filter { pokemon ->
                pokemon.name.contains(searchQuery, ignoreCase = true) ||
                        pokemon.id.toString().contains(searchQuery)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search Box
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Cari Pokemon (nama atau nomor)...") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Dropdown Filter Range
        ExposedDropdownMenuBox(
            expanded = expandedDropdown,
            onExpandedChange = { expandedDropdown = it }
        ) {
            OutlinedTextField(
                value = "Pokemon ${selectedRange.label}",
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors()
            )

            ExposedDropdownMenu(
                expanded = expandedDropdown,
                onDismissRequest = { expandedDropdown = false }
            ) {
                pokemonRanges.forEach { range ->
                    DropdownMenuItem(
                        text = { Text("Pokemon ${range.label}") },
                        onClick = {
                            selectedRange = range
                            expandedDropdown = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Loading atau Grid Pokemon
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Memuat Pokemon ${selectedRange.label}...")
                }
            }
        } else {
            // Grid Pokemon (3 kolom)
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                itemsIndexed(
                    items = filteredPokemon,
                    key = { _, pokemon -> pokemon.id }
                ) { _, pokemon ->
                    PokemonGridItem(pokemon)
                }

                // Jika hasil filter kosong
                if (filteredPokemon.isEmpty() && !isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Tidak ada Pokemon yang ditemukan",
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

// Function untuk load pokemon secara batch
suspend fun loadPokemonBatch(
    context: android.content.Context,
    startId: Int,
    endId: Int
): List<Pokemon> {
    return withContext(Dispatchers.IO) {
        (startId..endId).mapNotNull { id ->
            try {
                loadPokemon(context, id)
            } catch (e: Exception) {
                Log.e("PokemonListScreen", "Failed to load pokemon $id", e)
                null
            }
        }
    }
}

@Composable
fun PokemonGridItem(pokemon: Pokemon) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val imageUrl = pokemon.sprites.front_default

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
            .clickable {
                // Navigate ke PokemonDetailsActivity
                val intent = android.content.Intent(context, PokemonDetailsActivity::class.java)
                intent.putExtra("POKEMON_ID", pokemon.id)
                context.startActivity(intent)
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Nomor Pokemon
            Text(
                text = "#${pokemon.id.toString().padStart(4, '0')}",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Gambar Pokemon
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = pokemon.name,
                modifier = Modifier
                    .size(80.dp)
                    .weight(1f),
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    }
                },
                error = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("?", fontSize = 24.sp, color = Color.Gray)
                    }
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Nama Pokemon
            Text(
                text = pokemon.name.replaceFirstChar { it.uppercase() },
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}