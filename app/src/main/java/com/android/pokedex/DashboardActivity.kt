package com.android.pokedex

import android.content.Intent
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

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DashboardScreen(
                onPokemonClick = {
                    startActivity(Intent(this, PokemonListActivity::class.java))
                },
                onTypeClick = {
                    startActivity(Intent(this, TypeListActivity::class.java))
                }
            )
        }
    }
}

@Composable
fun DashboardScreen(
    onPokemonClick: () -> Unit,
    onTypeClick: () -> Unit
) {
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

        Button(onClick = onPokemonClick) {
            Text("Lihat Pokémon", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = onTypeClick) {
            Text("Lihat Type", fontSize = 20.sp)
        }
    }
}
