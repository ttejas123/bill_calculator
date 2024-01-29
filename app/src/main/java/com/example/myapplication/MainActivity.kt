package com.example.myapplication

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp("Android")
                }
            }
        }
    }
}

@Composable
fun MyApp(name: String, modifier: Modifier = Modifier) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {

        Column(
            modifier= Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Bill Calculator", style = TextStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            ), modifier=Modifier.padding(vertical = 30.dp))
            TipCalculator()

        }
    }
}

@Composable
fun TipCalculator() {

    val tipAmount = remember {
        mutableStateOf(0f)
    }

    val tipRange = remember {
        mutableStateOf(0f)
    }

    val split = remember {
        mutableStateOf(1)
    }

    val amount = remember {
        mutableStateOf("")
    }

    fun amountChange(it: String) {
        amount.value = it
    }

    fun getTipAmount(a:Float, b:String):Float {
        tipAmount.value = if(b.length > 0) ((b.toFloat() * a) / 100f) else 0f
        return if(tipAmount.value == Float.NaN) 0f else tipAmount.value
    }

    fun getFinalAmount():Float {
        if(amount.value.length == 0 || amount.value.toFloat() == 0f) return 0f;
        if(split.value == 0) return 0f;

        return ((amount.value.toFloat() + getTipAmount(tipAmount.value, amount.value)) / split.value).toFloat()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TotalHeader(amount = getFinalAmount())
        UserInputArea(amount = amount.value, amountChange = { amountChange(it) }, tipRange= tipRange, split= split, tipAmount= tipAmount, getTipAmount= {
            a, b -> getTipAmount(a, b)
        })
    }
}

@Composable
fun TotalHeader(amount: Float=0f) {
    Surface(modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp), color = colorResource(id = R.color.cyan),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 22.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Total Per Person", style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            ))

            Spacer(modifier = Modifier.height(4.dp))

            Text(text = "$${Math.round(amount)}", style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            ))
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun UserInputArea(
    amount: String = "",
    amountChange: (String) -> Unit = {},
    getTipAmount: (Float, String)-> Float,
    split: MutableState<Int>,
    tipRange: MutableState<Float>,
    tipAmount: MutableState<Float>
) {
    val keyboardController = LocalSoftwareKeyboardController.current;
    fun onPercentageChange(it:Float) {
        tipRange.value = it
    }

    fun onSplitInc() {
        split.value = split.value + 1
    }

    fun onSplitDec() {
        split.value = split.value - 1
    }

    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), shadowElevation = 12.dp) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            OutlinedTextField(
                value = amount,
                onValueChange = { amountChange(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(text = "Enter Your Amount")
                },
                keyboardOptions = KeyboardOptions(
                    autoCorrect = true,
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween){
                Text(text = "Split", style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.fillMaxWidth(.6f))

                customSplitController(split = split.value, onSplitDec = { onSplitDec() }, onSplitInc = { onSplitInc() })
            }
            customTipController(percentage = tipRange.value, tip = getTipAmount.invoke(tipRange.value, amount), onPercentageChange = { onPercentageChange(it) })
        }
    }
}

@Composable
fun customSplitController(split: Int=0, onSplitInc: ()-> Unit={}, onSplitDec: ()-> Unit={}) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Surface( shadowElevation = 1.dp, shape = RoundedCornerShape(34.dp), modifier = Modifier
            .width(40.dp)
            .height(40.dp)) {
            IconButton(onClick = { onSplitDec.invoke() }) {
                Icon(imageVector = Icons.Rounded.KeyboardArrowDown, contentDescription = "")
            }
        }

        Text(text = "${split}")

        Surface( shadowElevation = 1.dp, shape = RoundedCornerShape(34.dp), modifier = Modifier
            .width(40.dp)
            .height(40.dp)) {
            IconButton(onClick = { onSplitInc.invoke() }) {
                Icon(imageVector = Icons.Rounded.KeyboardArrowUp, contentDescription = "")
            }
        }

    }
}

@Composable
fun customTipController(
    tip: Float=0f,
    percentage:Float=0f,
    onPercentageChange: (Float)-> Unit={}
) {

    Column {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween){
            Text(text = "Tip", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.fillMaxWidth(.6f))

            Text(text = "$${tip}")
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(text = "${percentage}%")
        }

        Slider(value = percentage, onValueChange = {
            onPercentageChange(it)
        }, valueRange = 0f..100f, steps = 10, modifier = Modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth())
    }
}
