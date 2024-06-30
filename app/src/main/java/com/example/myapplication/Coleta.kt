package com.example.myapplication

import android.os.Parcel
import android.os.Parcelable
import java.time.ZonedDateTime

data class Coleta(
    val id: String = "",
    val usuarioId: String = "",
    val prestadorId: String = "",
    val prestadorNome: String = "",
    val endereco: String = "",
    val dataHora: ZonedDateTime,
    val aparelhoTipo: String = "",
    val status: Int = 0, // TODO: enum
    val avaliacao: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readValue(ZonedDateTime::class.java.classLoader) as ZonedDateTime,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(this.id)
        parcel.writeString(this.usuarioId)
        parcel.writeString(this.prestadorId)
        parcel.writeString(this.prestadorNome)
        parcel.writeString(this.endereco)
        parcel.writeValue(this.dataHora)
        parcel.writeString(this.aparelhoTipo)
        parcel.writeInt(this.status)
        parcel.writeInt(this.avaliacao)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Coleta> {
        override fun createFromParcel(parcel: Parcel): Coleta {
            return Coleta(parcel)
        }

        override fun newArray(size: Int): Array<Coleta?> {
            return arrayOfNulls(size)
        }
    }

}