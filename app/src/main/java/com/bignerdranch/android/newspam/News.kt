package com.bignerdranch.android.newspam

import androidx.room.Entity
import androidx.room.PrimaryKey;
import java.util.Date
import java.util.UUID
@Entity
data class News(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    var title: String = "",
    var date: Date = Date(),
    var isSolved: Boolean = false,
    var suspect: String = "")
{
    val photoFileName
    get() = "IMG_$id.jpg"

}