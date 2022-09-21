package com.example.notes

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.databinding.ItemNoteBinding

class NoteAdapter(var noteList: MutableList<Note>, private val listener: OnClickListener):
    RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    private lateinit var context: Context

    // ctrl+i para implementar metodos
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false)
        return ViewHolder(view)
    }

    // aquí es donde se modifica la vista
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = noteList.get(position)

        holder.setListener(note)

        holder.binding.tvDescription.text = note.description
        holder.binding.cbFinished.isChecked = note.isFinished

        if(note.isFinished){
            holder.binding.tvDescription.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                context.resources.getInteger(R.integer.description_size_finished).toFloat())
        } else {
            holder.binding.tvDescription.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                context.resources.getInteger(R.integer.description_size_default).toFloat())
        }
    }

    // indica cuántos elementos queremos ver en el listado
    override fun getItemCount(): Int = noteList.size

    fun add(note: Note) {
        noteList.add(note)
        // para que refresque la vista y se vean los cambios
        notifyDataSetChanged()
    }

    fun remove(note: Note) {
        noteList.remove(note)
        notifyDataSetChanged()
    }

    // es lo que nos va a permitir crear el itemNote dentro del RecyclerView
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val binding = ItemNoteBinding.bind(view)

        // estará pendiente del LongClick
        fun setListener(note: Note){
            // no usamos el setOnCheckListener ya que resultaria redundante
            binding.cbFinished.setOnClickListener{
                note.isFinished = (it as CheckBox).isChecked
                listener.onCheck(note)
            }
            binding.root.setOnLongClickListener {
                listener.onLongClick(note, this@NoteAdapter)
                true
            }
        }
    }
}