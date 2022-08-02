package com.example.pethaven.domain

class ChatMessage(val id: String = "",
                  val text: String = "",
                  val fromId: String = "",
                  val toId: String = "",
                  val timestamp: Long = -1L) {
//  constructor() : this("", "", "", "", -1)
}