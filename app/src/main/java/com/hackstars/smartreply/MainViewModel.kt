package com.hackstars.smartreply


import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.mlkit.nl.smartreply.SmartReply
import com.google.mlkit.nl.smartreply.SmartReplySuggestion
import com.google.mlkit.nl.smartreply.TextMessage

class MainViewModel : ViewModel() {

    private var _msgList = MutableLiveData<List<ConversationMsg>>()
    var messageList : MutableLiveData<List<ConversationMsg>> = _msgList

    private var _txt = mutableStateOf(TextFieldValue())
    var txt : MutableState<TextFieldValue> = _txt

    init {
        getMessage()
    }

    private fun getMessage() {
        _msgList.value = msgList
    }

    //List of messages to be passed to ML Kit
    var conversation : ArrayList<TextMessage> = ArrayList<TextMessage>()

    private var _suggList = MutableLiveData<List<SmartReplySuggestion>>()
    var suggList : MutableLiveData<List<SmartReplySuggestion>> = _suggList


    //Initialize the Conversation
    fun initializeConvo(){
        messageList.value?.forEach {body ->
            addConvo(body.msg, body.who)

        }
    }

    //Add conversation for the remote user
    private fun addConvo(msg: String, who: String) {
        conversation.add(TextMessage.createForRemoteUser(msg, System.currentTimeMillis(), who))
        getReply()
    }

    private fun getReply() {
        //instance of smart reply generator
        val smartReplyGenerator = SmartReply.getClient()
        //a conversation object is passed into the smartReplies method in order to generate replies
        smartReplyGenerator.suggestReplies(conversation).addOnSuccessListener { result ->
            if(result.suggestions.isNotEmpty()){
                suggList.value = result.suggestions
            }

        }
    }

    //Add suggested Messages to the conversation
    fun addConversation(conversationMsg: ConversationMsg){
        msgList.add(conversationMsg)
        initializeConvo()
        getMessage()
    }
}

//Create a dummy list for messages
var msgList : MutableList<ConversationMsg> = mutableListOf(
    ConversationMsg(
        msg = "Hi, Good Morning",
        who = "me"
    ),
    ConversationMsg(
        msg = "Oh hey -- how are you?",
        who = "friend"
    )
)

data class ConversationMsg (val msg :String, val who : String)
