package com.android.pokedex

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import com.android.pokedex.ui.theme.PokedexTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokedexTheme {
                DashboardScreen()
            }
        }
    }
}

@Composable
fun DashboardScreen() {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Warna tema Pokemon
    val pokemonRed = Color(0xFFDC0A2D)
    val pokemonYellow = Color(0xFFFFCB05)
    val pokemonBlue = Color(0xFF3B4CCA)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF5F5F5),
                        Color(0xFFE3F2FD)
                    )
                )
            )
    ) {
        if (isLandscape) {
            // Layout Landscape: Logo kiri, Button kanan
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bagian Kiri - Logo
                LogoSection(modifier = Modifier.weight(1f))

                Spacer(modifier = Modifier.width(32.dp))

                // Bagian Kanan - Buttons
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PokemonButton(
                        text = "Pokémon",
                        backgroundColor = pokemonRed,
                        onClick = {
                            val intent = Intent(context, PokemonListActivity::class.java)
                            context.startActivity(intent)
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    PokemonButton(
                        text = "Type",
                        backgroundColor = pokemonYellow,
                        textColor = Color.Black,
                        onClick = {
                            val intent = Intent(context, TypeListActivity::class.java)
                            context.startActivity(intent)
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Decorative Pokeballs
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        repeat(3) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(pokemonRed.copy(alpha = 0.3f))
                            )
                        }
                    }
                }
            }
        } else {
            // Layout Portrait: Vertikal seperti semula
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LogoSection()

                Spacer(modifier = Modifier.height(48.dp))

                PokemonButton(
                    text = "Pokémon",
                    backgroundColor = pokemonRed,
                    onClick = {
                        val intent = Intent(context, PokemonListActivity::class.java)
                        context.startActivity(intent)
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                PokemonButton(
                    text = "Type",
                    backgroundColor = pokemonYellow,
                    textColor = Color.Black,
                    onClick = {
                        val intent = Intent(context, TypeListActivity::class.java)
                        context.startActivity(intent)
                    }
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Decorative Pokeballs
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(pokemonRed.copy(alpha = 0.3f))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LogoSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo dengan background circle
        Box(
            modifier = Modifier
                .size(220.dp)
                .shadow(12.dp, CircleShape)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.pokedex_logo),
                contentDescription = "Pokedex Logo",
                modifier = Modifier.size(180.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Text(
            text = "POKÉDEX",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFDC0A2D),
            letterSpacing = 2.sp
        )
    }
}

@Composable
fun PokemonButton(
    text: String,
    backgroundColor: Color,
    textColor: Color = Color.White,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .shadow(8.dp, RoundedCornerShape(35.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(35.dp),
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        Text(
            text = text,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            letterSpacing = 1.sp
        )
    }
}