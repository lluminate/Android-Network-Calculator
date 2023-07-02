package com.networkcalculator.subnetcalculator

import android.os.Bundle
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.networkcalculator.subnetcalculator.ui.theme.SubnetCalculatorTheme
import kotlin.math.log2
import kotlin.math.pow

var maskBits = 0
var subnetMask = ""
var hosts = 0
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


fun maskBitsFunction(maskBits: Int = 0): Triple<Int, String, Int> {
    val hosts = ((2.0).pow(32 - maskBits) - 2).toInt()
    var binary = ""
    var subnetMask = ""
    repeat(maskBits) {
        binary += "1"
    }

    repeat(32 - maskBits) {
        binary += "0"
    }

    for (i in binary.chunked(8)) {
        val decimal = i.toInt(2)
        subnetMask += "$decimal."
    }

    subnetMask = subnetMask.dropLast(1)
    return Triple(maskBits, subnetMask, hosts)
}


fun subnetMaskFunction(subnetMask: String): Triple<Int, String, Int> {
    var maskBits = 0
    for (i in subnetMask.split(".").toTypedArray()) {
        val binary = Integer.toBinaryString(i.toInt()).count { it == '1' }
        maskBits += binary
    }

    val hosts = ((2.0).pow(32 - maskBits) - 2).toInt()
    return Triple(maskBits, subnetMask, hosts)
}


fun hostsFunction(hosts: Int = 0): Triple<Int, String, Int> {
    val maskBits = 32 - log2((hosts + 2.0)).toInt()
    var binary = ""
    var subnetMask = ""
    repeat(maskBits) {
        binary += "1"
    }

    repeat(32 - maskBits) {
        binary += "0"
    }

    for (i in binary.chunked(8)) {
        val decimal = i.toInt(2)
        subnetMask += "$decimal."
    }

    subnetMask = subnetMask.dropLast(1)
    return Triple(maskBits, subnetMask, hosts)
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
        MaskBitsDropdownMenu(
            listItems = listOf("30", "29", "28", "27", "26", "25", "24", "23", "22", "21", "20", "19", "18", "17", "16", "15", "14", "13", "12", "11", "10", "9", "8", "7", "6", "5", "4", "3", "2", "1"),
            menuLabel = "Mask Bits",
            modifier = Modifier
        )
        SubnetMaskDropdownMenu(
            listItems = listOf("255.255.255.252", "255.255.255.248", "255.255.255.240", "255.255.255.224", "255.255.255.192", "255.255.255.128", "255.255.255.0", "255.255.254.0", "255.255.252.0", "255.255.248.0", "255.255.240.0", "255.255.224.0", "255.255.192.0", "255.255.128.0", "255.255.0.0", "255.254.0.0", "255.252.0.0", "255.248.0.0", "255.240.0.0", "255.224.0.0", "255.192.0.0", "255.128.0.0", "255.0.0.0", "254.0.0.0", "252.0.0.0", "248.0.0.0", "240.0.0.0", "224.0.0.0", "192.0.0.0", "128.0.0.0"),
            menuLabel = "Subnet Mask",
            modifier = Modifier
        )
        HostsDropdownMenu(
            listItems = listOf("2", "6", "14", "30", "62", "126", "254", "510", "1022", "2046", "4094", "8190", "16382", "32766", "65534", "131070", "262142", "524286", "1048574", "2097150", "4194302", "8388606", "16777214", "33554430", "67108862", "134217726", "268435454", "536870910", "1073741822", "2147483646"),
            menuLabel = "Hosts/Net",
            modifier = Modifier
        )
        Spacer(modifier = Modifier.height(150.dp))
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaskBitsDropdownMenu(listItems: List<String>, menuLabel: String, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    var maskBitsSelectedItem by remember { mutableStateOf(listItems[0]) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        OutlinedTextField(
            value = maskBitsSelectedItem,
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
                        maskBitsSelectedItem = selectedOption
                        val (a, b, c) = maskBitsFunction(maskBitsSelectedItem.toInt())
                        maskBits = a
                        subnetMask = b
                        hosts = c
                        expanded = false
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubnetMaskDropdownMenu(listItems: List<String>, menuLabel: String, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    var subnetMaskSelectedItem by remember { mutableStateOf(listItems[0]) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        OutlinedTextField(
            value = subnetMaskSelectedItem,
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
                        subnetMaskSelectedItem = selectedOption
                        val (a, b, c) = subnetMaskFunction(subnetMaskSelectedItem)
                        maskBits = a
                        subnetMask = b
                        hosts = c
                        expanded = false
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostsDropdownMenu(listItems: List<String>, menuLabel: String, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    var hostsSelectedItem by remember { mutableStateOf(listItems[0]) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        OutlinedTextField(
            value = hostsSelectedItem,
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
                        hostsSelectedItem = selectedOption
                        val (a, b, c) = hostsFunction(hostsSelectedItem.toInt())
                        maskBits = a
                        subnetMask = b
                        hosts = c
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