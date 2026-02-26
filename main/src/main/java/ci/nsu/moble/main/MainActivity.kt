package ci.nsu.moble.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ci.nsu.moble.main.ui.theme.PracticeTheme
import ci.nsu.moble.main.data.ColorData
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PracticeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ColorSearchScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ColorSearchScreen(modifier: Modifier = Modifier) {
    var inputText by remember { mutableStateOf("") }
    var buttonColor by remember { mutableStateOf(Color(0xFF2196F3)) }
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Поиск цвета",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Введите название цвета") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val searchColor = inputText.lowercase().trim()
                val foundColor = ColorData.colors[searchColor]

                if (foundColor != null) {
                    buttonColor = Color(foundColor)
                    Log.d("ColorSearch", "Цвет '$searchColor' найден и применен")
                } else {
                    Log.e("ColorSearch", "Пользовательский цвет '$searchColor' не найден")
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Применить цвет", color = Color.White)
        }
        Spacer(modifier = Modifier.height(24.dp))

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp
        )

        Text(
            text = "Палитра цветов:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(
                items = ColorData.colors.toList(),
                key = { it.first }
            ) { (colorName, colorValue) ->
                ColorPaletteItem(
                    colorName = colorName,
                    colorValue = colorValue,
                    onColorSelected = { name, value ->
                        inputText = name
                        buttonColor = Color(value)
                        Log.d("ColorSearch", "Выбран цвет из палитры: '$name'")
                    }
                )
            }
            }
    }
}

@Composable
fun ColorPaletteItem(
    colorName: String,
    colorValue: Int,
    onColorSelected: (String, Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable { onColorSelected(colorName, colorValue) },
        colors = CardDefaults.cardColors(
            containerColor = Color(colorValue)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = colorName,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "#${Integer.toHexString(colorValue).substring(2).uppercase()}",
                color = Color.White.copy(alpha = 1.2f),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ColorSearchScreenPreview() {
    PracticeTheme {
        ColorSearchScreen()
    }
}