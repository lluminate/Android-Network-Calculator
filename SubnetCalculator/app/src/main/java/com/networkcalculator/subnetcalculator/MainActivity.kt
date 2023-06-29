package com.networkcalculator.subnetcalculator

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.networkcalculator.subnetcalculator.ui.theme.SubnetCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SubnetCalculatorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NetworkCalculatorLayout()
                }
            }
        }
    }
}


@Composable
fun NetworkCalculatorLayout(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier.padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Network Calculator",
            modifier = Modifier
                .padding(bottom = 16.dp)
        )
        EditIpAddress(
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth()
        )
        DropdownMenu(
            listItems = listOf("24", "25", "26", "27", "28", "29", "30"),
            menuLabel = "Mask Bits",
            modifier = Modifier
        )
        DropdownMenu(
            listItems = listOf("255.255.255.240"),
            menuLabel = "Subnet mask",
            modifier = Modifier
        )
        DropdownMenu(
            listItems = listOf("14"),
            menuLabel = "Hosts/Net",
            modifier = Modifier
        )
        Spacer(modifier = Modifier.height(150.dp))
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenu(listItems: List<String>, menuLabel: String, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf(listItems[0]) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        OutlinedTextField(
            value = selectedItem,
            onValueChange = { },
            readOnly = true,
            label = { Text(text = menuLabel) },
            modifier = modifier.menuAnchor(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            listItems.forEach { selectedOption ->
                DropdownMenuItem(
                    text = { Text(text = selectedOption) },
                    onClick = {
                        selectedItem = selectedOption
                        expanded = false
                    }
                )
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditIpAddress(modifier: Modifier = Modifier) {
    var ipInput by remember { mutableStateOf("") }
    OutlinedTextField(
        value = ipInput,
        label = { Text("IP Address") },
        onValueChange = { ipInput = it },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        maxLines = 1,
        modifier = Modifier
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Light Mode"
)
@Composable
fun SubnetCalculatorPreview() {
    SubnetCalculatorTheme {
        NetworkCalculatorLayout()
    }
}