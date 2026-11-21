package com.example.dogbreedsgallery.ui.favorites

// дориме...
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.dogbreedsgallery.AppDependencies
import com.example.dogbreedsgallery.data.local.BreedEntity

@Composable
fun FavoritesScreen(
    onBreedClick: (Int) -> Unit
) {
    val viewModel: FavoritesViewModel = viewModel {
        FavoritesViewModel(AppDependencies.breedRepository)
    }

    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {

            is FavoritesUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is FavoritesUiState.Empty -> {
                // Дружелюбное сообщение вместо пустого экрана
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Пока пусто",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(
                        "Добавляй породы в избранное\nчерез кнопку на экране деталей",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            is FavoritesUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = state.breeds,
                        key = { it.id }
                    ) { breed ->
                        FavoriteCard(
                            breed = breed,
                            onClick = { onBreedClick(breed.id) },
                            onRemoveClick = { viewModel.removeFromFavorites(breed) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteCard(
    breed: BreedEntity,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SubcomposeAsyncImage(
                model = breed.imageUrl,
                contentDescription = breed.name,
                modifier = Modifier.size(64.dp),
                contentScale = ContentScale.Crop
            ) {
                when (painter.state) {
                    is AsyncImagePainter.State.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    }
                    is AsyncImagePainter.State.Error -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Фото", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    else -> SubcomposeAsyncImageContent()
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = breed.name,
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.padding(2.dp))
                Text(
                    text = breed.temperament.ifEmpty { "Темперамент не указан" },
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
            }

            IconButton(onClick = onRemoveClick) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Убрать из избранного",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}