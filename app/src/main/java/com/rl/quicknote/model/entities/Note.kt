package com.rl.quicknote.model.entities

data class Note(
    val id: String,
    val title: String,
    val content: String,
    val timeStamp: String,
    val categories: List<Category> = emptyList(),
    val uid: String
)
