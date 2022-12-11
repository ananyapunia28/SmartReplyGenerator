package com.hackstars.smartreply

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hackstars.smartreply.ui.theme.Purple200
import com.hackstars.smartreply.ui.theme.SmartReplyTheme
import com.hackstars.smartreply.ui.theme.Teal200

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartReplyTheme{
                Surface(color = MaterialTheme.colors.background) {

                    val msg = viewModel.messageList.observeAsState(emptyList())

                    Scaffold(
                        topBar = {},
                        content = {
                            //The LazyColumn adds a vertically scrolling list, thus, we will add messages here
                            LazyColumn {
                                items(msg.value.size){ i ->
                                    ChatItem(msg = msg.value[i])
                                }
                            }
                        },
                        bottomBar = {
                            Column() {
                                SuggestionList(viewModel = viewModel)
                                Spacer(modifier = Modifier.height(10.dp))
                                TextInputField(viewModel = viewModel)
                            }

                        },
                        backgroundColor = Teal200,
                        )



                }
            }
        }
    }

    @Composable
    fun ChatItem(msg : ConversationMsg){
        Row(modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Transparent)){
            Icon(
                Icons.Default.Person,
                contentDescription = "",
                modifier = Modifier
                    .height(30.dp)
                    .width(30.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
                    .padding(4.dp)
                    .fillMaxHeight()
            )
            Card(
                Modifier
                    .padding(top = 10.dp, end = 10.dp, bottom = 10.dp)
                    .clip(CircleShape)){
                Text(text = msg.msg, modifier = Modifier.padding(10.dp))
            }

        }
    }

    @Composable
    fun SuggestionList(viewModel : MainViewModel){
        val suggList = viewModel.suggList.observeAsState()

        Row(horizontalArrangement = Arrangement.End, modifier = Modifier
            .padding(start = 4.dp, end = 4.dp)
            .fillMaxWidth()){
            if(suggList.value?.isNotEmpty() == true){
                suggList.value!!.forEach { item ->
                    CustomChip(text = item.text, modifier = Modifier
                        .padding(start = 4.dp, end = 4.dp)
                        .clickable {
                            viewModel.txt.value = TextFieldValue(item.text)
                        })

                }
            }
        }
    }

    @Composable
    fun CustomChip(text : String, modifier: Modifier = Modifier){
        Surface(
            color = Color.Transparent,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(width = 1.dp, color = Color.LightGray),
            modifier = modifier
        ) {
            Row(modifier = modifier){
                Text(text = text, color = Color.White, modifier = Modifier.padding(start = 10.dp,
                    end = 8.dp, top = 8.dp, bottom = 8.dp))
            }
        }
    }


    @Composable
    fun TextInputField(viewModel: MainViewModel){
        val textChangeVal = viewModel.txt
        val enableSend = remember { mutableStateOf(false) }

        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)){
            Row(modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {
                //Icon() is used to add different drawable icons on the screen
                Icon(
                    Icons.Default.AddCircle, contentDescription = "",
                    Modifier
                        .padding(end = 15.dp, top = 15.dp)
                        .clickable {}, tint = Purple200
                )
                //The next icon by default will be added next to the previous one here.
                Icon(
                    Icons.Default.Phone, contentDescription = "",
                    Modifier
                        .padding(end = 15.dp, top = 15.dp)
                        .clickable {}, tint = Purple200
                )
                //Add an editable text field using the OutlinedTextField()
                OutlinedTextField(
                    textStyle = TextStyle(color = Color.Blue),
                    value = textChangeVal.value,
                    onValueChange = {
                        textChangeVal.value = it
                        enableSend.value = textChangeVal.value.text.isNotEmpty()
                    } ,
                    //Add a text hint to text field
                    placeholder = {Text(text="Text (mtn)")},
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .background(color = Teal200)
                        .border(width = 1.dp, color = Teal200, shape = CircleShape),
                    //Trailing Icons are the icons which are displayed after the text field.
                    trailingIcon = {
                        Row(){
                            Icon(
                                Icons.Default.Menu, contentDescription = "",
                                Modifier
                                    .padding(end = 15.dp)
                                    .clickable {}, tint = Purple200
                            )
                            //If the value is null, ArrowBack icon will be displayed
                            if(!enableSend.value) Icon(
                                Icons.Default.ArrowBack, contentDescription = "",
                                Modifier
                                    .padding(end = 10.dp)
                                    .clickable {}, tint = Purple200
                            )
                            //If the value is not null, the Send icon will be displayed
                            if(enableSend.value) Icon(
                                Icons.Default.Send, contentDescription = "",
                                Modifier
                                    .padding(end = 10.dp)
                                    .clickable {
                                        if (textChangeVal.value.text.isNotEmpty()) {
                                            viewModel.addConversation(
                                                ConversationMsg(
                                                    msg = textChangeVal.value.text, who = "me"
                                                )
                                            )
                                        }
                                    }, tint = Purple200
                            )
                        }
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = Color.Black,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = Color.Transparent
                    )



                )
            }
        }
    }

}