package com.android.pokedex

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.android.pokedex.model.Pokemon
import com.android.pokedex.ui.theme.PokedexTheme
import com.android.pokedex.utils.getTypeColor
import com.android.pokedex.utils.loadPokemon
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class TypeListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokedexTheme {
                // Gunakan Surface dengan MaterialTheme colors agar mengikuti theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TypeChartScreen()
                }
            }
        }
    }
}

data class TypeEffectiveness(
    val typeName: String,
    val weakAgainst: List<String>,
    val resistantTo: List<String>,
    val immuneTo: List<String>,
    val superEffectiveAgainst: List<String>,
    val notVeryEffectiveAgainst: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypeChartScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<TypeEffectiveness?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current

    // Deteksi dark mode
    val isDarkMode = isSystemInDarkTheme()

    val pokemonTypes = listOf(
        "Normal", "Fire", "Water", "Electric", "Grass", "Ice",
        "Fighting", "Poison", "Ground", "Flying", "Psychic", "Bug",
        "Rock", "Ghost", "Dragon", "Dark", "Steel", "Fairy"
    )

    val typeEffectivenessMap = remember {
        pokemonTypes.associateWith { type ->
            loadTypeEffectiveness(context, type)
        }
    }

    // Simulate loading delay
    LaunchedEffect(Unit) {
        delay(1500)
        isLoading = false
    }

    val filteredTypes = remember(searchQuery) {
        if (searchQuery.isBlank()) pokemonTypes
        else pokemonTypes.filter { it.contains(searchQuery, ignoreCase = true) }
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val gridColumns = if (isLandscape) 6 else 3

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Bagian atas (Search) - mengikuti dark mode
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Search Box - mengikuti theme
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(id = R.string.search_type),) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Box putih untuk Type Grid - SELALU PUTIH bahkan di dark mode
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
                    .padding(16.dp)
            ) {
                // CEK LOADING
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Memuat Type Pokemon...",
                                color = Color.Black // Text hitam karena background putih
                            )
                        }
                    }
                } else {
                    // Grid dinamis berdasarkan orientasi
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(gridColumns),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(filteredTypes) { typeName ->
                            TypeCard(
                                typeName = typeName,
                                onClick = { selectedType = typeEffectivenessMap[typeName] }
                            )
                        }
                    }

                    if (filteredTypes.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Tidak ada type yang ditemukan",
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }

    if (selectedType != null) {
        TypeEffectivenessDialog(
            typeEffectiveness = selectedType!!,
            onDismiss = { selectedType = null }
        )
    }
}

@Composable
fun TypeCard(
    typeName: String,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val pngPath = "file:///android_asset/type/${typeName.lowercase()}.png"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2D2D2D) // Card tetap gelap untuk kontras dengan icon
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(pngPath)
                    .crossfade(true)
                    .build(),
                contentDescription = "$typeName type icon",
                modifier = Modifier
                    .size(56.dp)
                    .weight(1f),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = typeName,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun TypeEffectivenessDialog(
    typeEffectiveness: TypeEffectiveness,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.75f)
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        TypeIconSvg(
                            typeName = typeEffectiveness.typeName,
                            size = 28.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${typeEffectiveness.typeName} Type",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = getTypeColor(typeEffectiveness.typeName.lowercase())
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (typeEffectiveness.superEffectiveAgainst.isNotEmpty()) {
                        item {
                            EffectivenessSectionWithIcons(
                                title = stringResource(id = R.string.supere),
                                multiplier = "2×",
                                types = typeEffectiveness.superEffectiveAgainst,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }

                    if (typeEffectiveness.notVeryEffectiveAgainst.isNotEmpty()) {
                        item {
                            EffectivenessSectionWithIcons(
                                title = stringResource(id = R.string.notvery),
                                multiplier = "0.5×",
                                types = typeEffectiveness.notVeryEffectiveAgainst,
                                color = Color(0xFFFF9800)
                            )
                        }
                    }

                    if (typeEffectiveness.weakAgainst.isNotEmpty()) {
                        item {
                            EffectivenessSectionWithIcons(
                                title = stringResource(id = R.string.weak),
                                multiplier = "2×",
                                types = typeEffectiveness.weakAgainst,
                                color = Color(0xFFF44336)
                            )
                        }
                    }

                    if (typeEffectiveness.resistantTo.isNotEmpty()) {
                        item {
                            EffectivenessSectionWithIcons(
                                title = stringResource(id = R.string.resis),
                                multiplier = "0.5×",
                                types = typeEffectiveness.resistantTo,
                                color = Color(0xFF2196F3)
                            )
                        }
                    }

                    if (typeEffectiveness.immuneTo.isNotEmpty()) {
                        item {
                            EffectivenessSectionWithIcons(
                                title = stringResource(id = R.string.imune),
                                multiplier = "0×",
                                types = typeEffectiveness.immuneTo,
                                color = Color(0xFF9E9E9E)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TypeIconSvg(
    typeName: String,
    size: androidx.compose.ui.unit.Dp
) {
    val context = LocalContext.current
    val pngPath = "file:///android_asset/type/${typeName.lowercase()}.png"

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(pngPath)
            .crossfade(true)
            .build(),
        contentDescription = "$typeName icon",
        modifier = Modifier.size(size),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun EffectivenessSectionWithIcons(
    title: String,
    multiplier: String,
    types: List<String>,
    color: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = multiplier,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            types.forEach { type ->
                TypeIconOnly(typeName = type)
            }
        }
    }
}

@Composable
fun TypeIconOnly(
    typeName: String
) {
    val context = LocalContext.current
    val pngPath = "file:///android_asset/type/${typeName.lowercase()}.png"

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(pngPath)
            .crossfade(true)
            .build(),
        contentDescription = "$typeName icon",
        modifier = Modifier.size(32.dp),
        contentScale = ContentScale.Fit
    )
}

fun loadTypeEffectiveness(context: android.content.Context, typeName: String): TypeEffectiveness {
    return try {
        val jsonString = context.assets.open("type/${typeName}.json")
            .bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(jsonString)
        val damageRelations = jsonObject.getJSONObject("damage_relations")

        TypeEffectiveness(
            typeName = typeName,
            weakAgainst = jsonArrayToList(damageRelations.getJSONArray("double_damage_from")),
            resistantTo = jsonArrayToList(damageRelations.getJSONArray("half_damage_from")),
            immuneTo = jsonArrayToList(damageRelations.getJSONArray("no_damage_from")),
            superEffectiveAgainst = jsonArrayToList(damageRelations.getJSONArray("double_damage_to")),
            notVeryEffectiveAgainst = jsonArrayToList(damageRelations.getJSONArray("half_damage_to"))
        )
    } catch (e: Exception) {
        TypeEffectiveness(
            typeName = typeName,
            weakAgainst = emptyList(),
            resistantTo = emptyList(),
            immuneTo = emptyList(),
            superEffectiveAgainst = emptyList(),
            notVeryEffectiveAgainst = emptyList()
        )
    }
}

fun jsonArrayToList(jsonArray: org.json.JSONArray): List<String> {
    val list = mutableListOf<String>()
    for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        list.add(obj.getString("name").replaceFirstChar { it.uppercase() })
    }
    return list
}