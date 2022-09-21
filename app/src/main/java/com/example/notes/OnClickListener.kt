package com.example.notes

interface OnClickListener {
    fun onLongClick(note: Note, currentAdapter: NoteAdapter)
    fun onCheck(note: Note)
}