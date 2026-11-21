package com.example.dogbreedsgallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.dogbreedsgallery.navigation.NavKey
import com.example.dogbreedsgallery.ui.breeddetails.BreedDetailsScreen
import com.example.dogbreedsgallery.ui.breedlist.BreedListScreen
import com.example.dogbreedsgallery.ui.favorites.FavoritesScreen

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
    val backStack: SnapshotStateList<NavKey> = rememberSaveable {
        listOf<NavKey>(NavKey.BreedList).toMutableStateList()
    }

    val currentScreen: NavKey = backStack.lastOrNull() ?: NavKey.BreedList

    val previousScreen: NavKey? = backStack.getOrNull(backStack.lastIndex - 1)

    val homeSelected: Boolean = currentScreen is NavKey.BreedList ||
            (currentScreen is NavKey.BreedDetails && previousScreen !is NavKey.Favorites)
    val favoritesSelected: Boolean = currentScreen is NavKey.Favorites ||
            (currentScreen is NavKey.BreedDetails && previousScreen is NavKey.Favorites)

    fun goBack() {
        android.util.Log.d("DogNav", "goBack, стек: $backStack")
        backStack.removeLastOrNull()
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = homeSelected,
                    onClick = { backStack.add(NavKey.BreedList) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Главная") },
                    label = { Text("Главная") }
                )
                NavigationBarItem(
                    selected = favoritesSelected,
                    onClick = { backStack.add(NavKey.Favorites) },
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
                    BreedListScreen(
                        onBreedClick = { breedId ->
                            backStack.add(NavKey.BreedDetails(breedId))
                        }
                    )
                }
                entry<NavKey.BreedDetails> { key ->
                    BreedDetailsScreen(
                        breedId = key.breedId,
                        onBackClick = { goBack() }
                    )
                }
                entry<NavKey.Favorites> {
                    FavoritesScreen(
                        onBreedClick = { breedId ->
                            backStack.add(NavKey.BreedDetails(breedId))
                        }
                    )
                }
            }
        )
    }
}