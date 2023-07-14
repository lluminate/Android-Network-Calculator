package com.networkcalculator.subnetcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Divider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.networkcalculator.subnetcalculator.ui.theme.SubnetCalculatorTheme
import java.util.regex.Pattern
import kotlin.math.log2
import kotlin.math.pow

var maskBits by mutableStateOf(0)
var subnetMask by mutableStateOf("")
var hosts by mutableStateOf(0)
var ipInput by mutableStateOf("192.168.0.1")
var errorState by mutableStateOf("IP Address")

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
            text = "NETWORK CALCULATOR",
            modifier = Modifier.padding(bottom = 16.dp),
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold
        )
        EditIpAddress(
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth()
        )
        MaskBitsDropdownMenu()
        SubnetMaskDropdownMenu()
        HostsDropdownMenu()
        Spacer(modifier = Modifier.height(16.dp))
        ResultsDataTable(modifier = Modifier.padding(bottom = 32.dp))
        Spacer(modifier = Modifier.height(80.dp))
    }
}


@Composable
fun ResultsDataTable(modifier: Modifier = Modifier) {
    val (lowerRange, upperRange, broadcast) = ipRange()
    var updatedResultsData: List<Pair<String, String>>

    if () {
        updatedResultsData = listOf(
            "Network:    " to "${ipToNetwork(ipInput)}/$maskBits",
            "Netmask:    " to subnetMask,
            "Ip range:    " to "$lowerRange \n$upperRange",
            "Hosts/Net:    " to "$hosts",
            "Broadcast:    " to broadcast
        )
    } else if () {
        updatedResultsData = listOf(
            "Network:    " to "${ipToHex(ipToNetwork(ipInput))}/${toToDigitHex(maskBits)}",
            "Netmask:    " to ipToHex(subnetMask),
            "Ip range:    " to "${ipToHex(lowerRange)} \n${ipToHex(upperRange)}",
            "Hosts/Net:    " to "${toToDigitHex(hosts)}",
            "Broadcast:    " to ipToHex(broadcast)
        )
    } else if () {
        updatedResultsData = listOf(
            "Network:    " to "${ipToNetwork(ipInput)}/$maskBits",
            "Netmask:    " to subnetMask,
            "Ip range:    " to "$lowerRange \n$upperRange",
            "Hosts/Net:    " to "$hosts",
            "Broadcast:    " to broadcast
        )
    }

    LazyColumn {
        itemsIndexed(updatedResultsData) { index, item ->
            Row(modifier = Modifier.padding(5.dp)) {
                Text(
                    text = item.first,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.Light
                )
                Text(
                    text = item.second,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold
                )
            }
            if (index < updatedResultsData.size - 1) {
                Divider(modifier = Modifier.padding(horizontal = 25.dp))
            }
        }
    }
}


fun ipToHex(ip: String): String {
    var hex = ""
    var tempString = ""
    for (i in ip.split(".").toTypedArray()) {
        repeat(2 - Integer.toHexString(i.toInt()).length) {
            tempString += "0"
        }
        hex += tempString + Integer.toHexString(i.toInt()) + "."
        tempString = ""
    }
    return hex.dropLast(1)
}


fun toToDigitHex(num: Int) {
    var hex = ""
    var tempString = ""
    if (Integer.toHexString(num).length < 2) {
        repeat(2 - Integer.toHexString(num).length) {
            tempString += "0"
        }
        hex += tempString + Integer.toHexString(num)
        tempString = ""
    } else {
        hex += Integer.toHexString(num)
    }
}


fun ipToNetwork(ip: String): String {
    return binaryToIp(ipToBinary(ip).dropLast(8) + "00000000")
}


fun ipToBinary(ip: String): String {
    var binary = ""
    var tempString = ""
    for (i in ip.split(".").toTypedArray()) {
        repeat(8 - Integer.toBinaryString(i.toInt()).length) {
            tempString += "0"
        }
        binary += tempString + Integer.toBinaryString(i.toInt())
        tempString = ""
    }
    return binary
}


fun binaryToIp(binary: String): String {
    var decimal = ""
    for (i in binary.chunked(8)) {
        decimal += i.toInt(2).toString() + "."
    }
    return decimal.dropLast(1)
}


fun ipRange(): Triple<String, String, String> {
    var lower = ""
    var upper = ""

    repeat(32 - maskBits) {
        upper += "1"
        lower += "0"
    }
    val broadcast = binaryToIp(ipToBinary(ipInput).dropLast(32 - maskBits) + upper)
    lower = lower.dropLast(1) + "1"
    upper = upper.dropLast(1) + "0"

    return Triple(
        binaryToIp(ipToBinary(ipInput).dropLast(32 - maskBits) + lower),
        binaryToIp(ipToBinary(ipInput).dropLast(32 - maskBits) + upper),
        broadcast
    )
}


fun isValidIPAddress(ip: String): Boolean {
    val reg0To255 = ("(\\d{1,2}|(0|1)\\" + "d{2}|2[0-4]\\d|25[0-5])")
    return Pattern.compile("$reg0To255\\.$reg0To255\\.$reg0To255\\.$reg0To255").matcher(ip)
        .matches()
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaskBitsDropdownMenu(modifier: Modifier = Modifier) {
    val listItems = listOf(
        "30",
        "29",
        "28",
        "27",
        "26",
        "25",
        "24",
        "23",
        "22",
        "21",
        "20",
        "19",
        "18",
        "17",
        "16",
        "15",
        "14",
        "13",
        "12",
        "11",
        "10",
        "9",
        "8",
        "7",
        "6",
        "5",
        "4",
        "3",
        "2",
        "1"
    )
    var expanded by remember { mutableStateOf(false) }
    var maskBitsSelectedItem by remember { mutableStateOf(listItems[0]) }
    val (a, b, c) = maskBitsFunction(maskBitsSelectedItem.toInt())
    maskBits = a
    subnetMask = b
    hosts = c
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        OutlinedTextField(
            value = maskBits.toString(),
            onValueChange = { },
            readOnly = true,
            label = { Text(text = "Mask Bits") },
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
                        expanded = false
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubnetMaskDropdownMenu(modifier: Modifier = Modifier) {
    val listItems = listOf(
        "255.255.255.252",
        "255.255.255.248",
        "255.255.255.240",
        "255.255.255.224",
        "255.255.255.192",
        "255.255.255.128",
        "255.255.255.0",
        "255.255.254.0",
        "255.255.252.0",
        "255.255.248.0",
        "255.255.240.0",
        "255.255.224.0",
        "255.255.192.0",
        "255.255.128.0",
        "255.255.0.0",
        "255.254.0.0",
        "255.252.0.0",
        "255.248.0.0",
        "255.240.0.0",
        "255.224.0.0",
        "255.192.0.0",
        "255.128.0.0",
        "255.0.0.0",
        "254.0.0.0",
        "252.0.0.0",
        "248.0.0.0",
        "240.0.0.0",
        "224.0.0.0",
        "192.0.0.0",
        "128.0.0.0"
    )
    var expanded by remember { mutableStateOf(false) }
    var subnetMaskSelectedItem by remember { mutableStateOf(listItems[0]) }
    val (a, b, c) = subnetMaskFunction(subnetMaskSelectedItem)
    maskBits = a
    subnetMask = b
    hosts = c
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        OutlinedTextField(
            value = subnetMask,
            onValueChange = { },
            readOnly = true,
            label = { Text(text = "Subnet Mask") },
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
                        expanded = false
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostsDropdownMenu(modifier: Modifier = Modifier) {
    val listItems = listOf(
        "2",
        "6",
        "14",
        "30",
        "62",
        "126",
        "254",
        "510",
        "1022",
        "2046",
        "4094",
        "8190",
        "16382",
        "32766",
        "65534",
        "131070",
        "262142",
        "524286",
        "1048574",
        "2097150",
        "4194302",
        "8388606",
        "16777214",
        "33554430",
        "67108862",
        "134217726",
        "268435454",
        "536870910",
        "1073741822",
        "2147483646"
    )
    var expanded by remember { mutableStateOf(false) }
    var hostsSelectedItem by remember { mutableStateOf(listItems[0]) }
    val (a, b, c) = hostsFunction(hostsSelectedItem.toInt())
    maskBits = a
    subnetMask = b
    hosts = c
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        OutlinedTextField(
            value = hosts.toString(),
            onValueChange = {},
            readOnly = true,
            label = { Text(text = "Hosts/Net") },
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
    var tempIpInput by remember { mutableStateOf(ipInput) }
    OutlinedTextField(
        value = tempIpInput,
        label = { Text(errorState) },
        onValueChange = {
            tempIpInput = it
            if (isValidIPAddress(tempIpInput)) {
                ipInput = tempIpInput
                errorState = "IP Address"
            } else {
                errorState = "Invalid IP Address"
            }
        },
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