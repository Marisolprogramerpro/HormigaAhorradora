package com.example.hormigaahorradora.core.operations
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hormigaahorradora.data.model.Gasto
import com.example.hormigaahorradora.databinding.ItemGastoBinding

class Gastosadapter {

    class GastosAdapter(
        private val onEditClick: (Gasto) -> Unit,
        private val onDeleteClick: (Gasto) -> Unit
    ) : ListAdapter<Gasto, GastosAdapter.GastoViewHolder>(DIFF) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GastoViewHolder {
            val binding = ItemGastoBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return GastoViewHolder(binding)
        }

        override fun onBindViewHolder(holder: GastoViewHolder, position: Int) {
            holder.bind(getItem(position))
        }

        inner class GastoViewHolder(
            private val binding: ItemGastoBinding
        ) : RecyclerView.ViewHolder(binding.root) {

            fun bind(gasto: Gasto) {
                binding.tvConcepto.text = gasto.concepto
                binding.tvMonto.text = "-$%.2f".format(gasto.monto)
                binding.tvFecha.text = gasto.fecha
                binding.tvNota.text = gasto.nota.ifBlank { "Sin nota" }

                binding.btnEditar.setOnClickListener { onEditClick(gasto) }
                binding.btnEliminar.setOnClickListener { onDeleteClick(gasto) }
            }
        }

        companion object {
            private val DIFF = object : DiffUtil.ItemCallback<Gasto>() {
                override fun areItemsTheSame(oldItem: Gasto, newItem: Gasto) =
                    oldItem.id == newItem.id
                override fun areContentsTheSame(oldItem: Gasto, newItem: Gasto) =
                    oldItem == newItem
            }
        }
    }
}