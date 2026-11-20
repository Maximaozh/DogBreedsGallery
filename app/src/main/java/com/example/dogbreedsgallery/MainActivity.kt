package com.example.dogbreedsgallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.autoSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.dogbreedsgallery.navigation.NavKey

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        AppDependencies.init(applicationContext)

        setContent {
            MaterialTheme {
                DogBreedsApp()
            }
        }
    }
}
@Composable
fun DogBreedsApp() {
    val backStack: SnapshotStateList<NavKey> = rememberSaveable(saver = autoSaver()) {
        listOf<NavKey>(NavKey.BreedList).toMutableStateList()
    }

    var selectedTab by remember { mutableStateOf<NavKey>(NavKey.BreedList) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == NavKey.BreedList,
                    onClick = {
                        selectedTab = NavKey.BreedList
                        backStack.clear()
                        backStack.add(NavKey.BreedList)
                    },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Главная") },
                    label = { Text("Главная") }
                )
                NavigationBarItem(
                    selected = selectedTab == NavKey.Favorites,
                    onClick = {
                        selectedTab = NavKey.Favorites
                        backStack.clear()
                        backStack.add(NavKey.Favorites)
                    },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Избранное") },
                    label = { Text("Избранное") }
                )
            }
        }
    ) { innerPadding ->
        NavDisplay(
            backStack = backStack,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            entryProvider = entryProvider {
                entry<NavKey.BreedList> {
                    BreedListScreenPlaceholder()
                }
                entry<NavKey.BreedDetails> { key ->
                    BreedDetailsScreenPlaceholder(breedId = key.breedId)
                }
                entry<NavKey.Favorites> {
                    FavoritesScreenPlaceholder()
                }
            }
        )
    }
}

@Composable
fun BreedListScreenPlaceholder() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Здесь будет список пород")
    }
}

@Composable
fun BreedDetailsScreenPlaceholder(breedId: Int) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Детали породы #$breedId")
    }
}

@Composable
fun FavoritesScreenPlaceholder() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Здесь будут избранные породы")
    }
}