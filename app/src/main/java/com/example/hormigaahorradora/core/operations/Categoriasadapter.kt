package com.example.hormigaahorradora.core.operations

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hormigaahorradora.databinding.ItemCategoriaBinding
import com.example.hormigaahorradora.core.model.Categoria

class CategoriasAdapter(
    private val onItemClick: (Categoria) -> Unit
) : ListAdapter<Categoria, CategoriasAdapter.CategoriaViewHolder>(DIFF) {

    private var totales: Map<String, Double> = emptyMap()

    fun submitTotales(nuevos: Map<String, Double>) {
        totales = nuevos
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val binding = ItemCategoriaBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoriaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CategoriaViewHolder(
        private val binding: ItemCategoriaBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(categoria: Categoria) {
            val totalGastado = totales[categoria.id] ?: 0.0
            val presupuesto = categoria.presupuestoMensual
            val porcentaje = if (presupuesto > 0)
                ((totalGastado / presupuesto) * 100).toInt().coerceAtMost(100) else 0

            binding.tvIcono.text = categoria.icono
            binding.tvNombre.text = categoria.nombre
            binding.tvDescripcion.text = categoria.descripcion
            binding.tvTotalGastado.text = "$%.2f".format(totalGastado)
            binding.tvPresupuesto.text = "de $%.2f".format(presupuesto)
            binding.progressBar.progress = porcentaje




            val colorBarra = when {
                porcentaje >= 100 -> "#E53935".toColorInt()
                porcentaje >= 80  -> "#FF9800".toColorInt()
                else              -> "#43A047".toColorInt()
            }
            binding.progressBar.progressTintList = ColorStateList.valueOf(colorBarra)


            val estado = when {
                porcentaje >= 100 -> "¡Límite alcanzado!"
                porcentaje >= 80  -> "Casi al límite"
                else              -> "En control"
            }
            binding.tvEstado.text = estado
            binding.tvEstado.setTextColor(colorBarra)

            binding.root.setOnClickListener { onItemClick(categoria) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Categoria>() {
            override fun areItemsTheSame(oldItem: Categoria, newItem: Categoria) =
                oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Categoria, newItem: Categoria) =
                oldItem == newItem
        }
    }
}
