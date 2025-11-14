package com.android.pokedex

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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

// Daftar semua tipe Pokemon
val pokemonTypes = listOf(
    "normal", "fire", "water", "electric", "grass", "ice",
    "fighting", "poison", "ground", "flying", "psychic", "bug",
    "rock", "ghost", "dragon", "dark", "steel", "fairy"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var pokemonList by remember { mutableStateOf<List<Pokemon>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf<String?>(null) }

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

    val context = LocalContext.current

    // Deteksi orientasi landscape
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Load pokemon saat range berubah
    LaunchedEffect(selectedRange) {
        isLoading = true
        pokemonList = loadPokemonBatch(context, selectedRange.start, selectedRange.end)
        isLoading = false
    }

    // Filter pokemon berdasarkan search query dan tipe
    val filteredPokemon = remember(searchQuery, pokemonList, selectedType) {
        var result = pokemonList

        // Filter by search query
        if (searchQuery.isNotBlank()) {
            result = result.filter { pokemon ->
                pokemon.name.contains(searchQuery, ignoreCase = true) ||
                        pokemon.id.toString().contains(searchQuery)
            }
        }

        // Filter by type
        if (selectedType != null) {
            result = result.filter { pokemon ->
                pokemon.types?.any { it.type.name.equals(selectedType, ignoreCase = true) } == true
            }
        }

        result
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search Box dan Dropdown - Bersebelahan saat landscape
        if (isLandscape) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Search Box (Kiri)
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Cari Pokemon...") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )

                // Dropdown Filter Range (Kanan)
                ExposedDropdownMenuBox(
                    expanded = expandedDropdown,
                    onExpandedChange = { expandedDropdown = it },
                    modifier = Modifier.weight(1f)
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
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
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
            }
        } else {
            // Portrait mode - vertical layout
            // Search Box
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Cari Pokemon (nama atau nomor)...") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
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
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
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
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Filter by Type dengan scroll horizontal
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter Berdasarkan Tipe:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                // Reset filter button
                if (selectedType != null) {
                    TextButton(
                        onClick = { selectedType = null },
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Reset filter",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Type chips dengan horizontal scroll
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                pokemonTypes.forEach { type ->
                    TypeFilterChip(
                        typeName = type,
                        isSelected = selectedType == type,
                        onClick = {
                            selectedType = if (selectedType == type) null else type
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
            // Info jumlah hasil
            if (selectedType != null || searchQuery.isNotBlank()) {
                Text(
                    text = "Menampilkan ${filteredPokemon.size} Pokemon",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Grid Pokemon (3 kolom portrait, 5 kolom landscape)
            LazyVerticalGrid(
                columns = GridCells.Fixed(if (isLandscape) 5 else 3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                itemsIndexed(
                    items = filteredPokemon,
                    key = { _, pokemon -> pokemon.id }
                ) { _, pokemon ->
                    PokemonGridItem(pokemon, isLandscape)
                }
            }

            // Jika hasil filter kosong
            if (filteredPokemon.isEmpty() && !isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tidak ada Pokemon yang ditemukan",
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun TypeFilterChip(
    typeName: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val typeColor = getTypeColor(typeName)
    val backgroundColor = if (isSelected) typeColor else Color(0xFFE0E0E0)
    val textColor = if (isSelected) Color.White else Color.Black

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = typeName.replaceFirstChar { it.uppercase() },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = textColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 13.sp
        )
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
fun PokemonGridItem(pokemon: Pokemon, isLandscape: Boolean = false) {
    val context = LocalContext.current
    val imageUrl = pokemon.sprites.front_default

    // Ukuran gambar lebih besar saat landscape
    val imageSize = if (isLandscape) 100.dp else 80.dp

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
                    .size(imageSize)
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

// Helper function untuk warna type - menggunakan fungsi dari PokemonDetailsActivity
// Pastikan fungsi getTypeColor() sudah ada di PokemonDetailsActivity.kt
// atau pindahkan ke file utils terpisah untuk menghindari duplikasi