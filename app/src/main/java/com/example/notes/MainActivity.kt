package com.example.notes

import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notes.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var notesAdapter: NoteAdapter
    private lateinit var notesFinishedAdapter: NoteAdapter
    private lateinit var database: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // para inicializar la base de datos
        database = DatabaseHelper(this)

        notesAdapter = NoteAdapter(mutableListOf(), this)
        // aplicamos la info a la vista
        binding.rvNotes.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = notesAdapter
        }

        notesFinishedAdapter = NoteAdapter(mutableListOf(), this)
        binding.rvNotesFinished.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = notesFinishedAdapter
        }

        // para agregar una nota nueva
        binding.btnAdd.setOnClickListener{
            if(binding.etDescription.text.toString().isNotBlank()){
                val note = Note(description = binding.etDescription.text.toString().trim())
                note.id = database.insertNote(note)

                if(note.id !== Constants.ID_ERROR){
                    addNoteAuto(note)
                    // para limpiar el campo
                    binding.etDescription.text?.clear()
                    showMessage(R.string.msg_write_database_success)
                } else {
                    showMessage(R.string.msg_write_database_error)
                }
            } else {
                binding.etDescription.error = getString(R.string.validation_field_required)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        getData()
    }

    private fun getData() {
        /*val data = mutableListOf(
            Note(1, "Estudiar"),
            Note(2,"Ir al mercado"),
            Note(3,"Hola", true))*/

        val data = database.getAllNotes()
        data.forEach{note ->
            addNoteAuto(note)
        }
    }

    private fun addNoteAuto(note: Note) {
        // separa las notas entre las terminadas y las no
        if(note.isFinished){
            notesFinishedAdapter.add(note)
        } else {
            notesAdapter.add(note)
        }
    }

    private fun deleteNoteAuto(note: Note) {
        if(note.isFinished){
            notesAdapter.remove(note)
        } else {
            notesFinishedAdapter.remove(note)
        }
    }

    override fun onCheck(note: Note) {
        if(database.updateNote(note)){
            deleteNoteAuto(note)
            addNoteAuto(note)
        } else {
            showMessage(R.string.msg_write_database_error)
        }
    }

    override fun onLongClick(note: Note, currentAdapter: NoteAdapter) {
        val builder = AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_title))
            .setPositiveButton(getString(R.string.dialog_ok),{ dialogInterface, i ->
                if(database.deleteNote(note)){
                    currentAdapter.remove(note)
                    showMessage(R.string.msg_write_database_success)
                } else {
                    showMessage(R.string.msg_write_database_error)
                }
            })
            .setNegativeButton(getString(R.string.dialog_cancel),null)

        builder.create().show()
    }

    private fun showMessage(msgRes: Int){
        Snackbar.make(binding.root, getString(msgRes), Snackbar.LENGTH_SHORT).show()
    }
}