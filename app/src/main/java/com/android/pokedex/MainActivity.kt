package com.android.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
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

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.pokedex_logo),
            contentDescription = null,
            modifier = Modifier.size(180.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Tombol LIHAT POKEMON
        Button(onClick = {
            val intent = Intent(context, PokemonListActivity::class.java)
            context.startActivity(intent)
        }) {
            Text("Lihat Pokémon", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Tombol LIHAT TYPE
        Button(onClick = {
            val intent = Intent(context, TypeListActivity::class.java)
            context.startActivity(intent)
        }) {
            Text("Lihat Type", fontSize = 20.sp)
        }
    }
}
