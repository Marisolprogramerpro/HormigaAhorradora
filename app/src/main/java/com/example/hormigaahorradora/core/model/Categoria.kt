package com.example.hormigaahorradora.core.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class CategoriasResponse(
    @SerializedName("categorias") val categorias: List<Categoria>
)

@Parcelize
data class Categoria(
    @SerializedName("id") val id: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("icono") val icono: String,
    @SerializedName("color") val color: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("presupuesto_mensual") val presupuestoMensual: Double
) : Parcelable
