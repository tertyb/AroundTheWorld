package com.harelshaigal.aroundtw.data.user

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class User(
    @PrimaryKey
    val uid: String,
    val fullName: String? = null,
    val email: String? = null,
    var imageUri: String? = null
) {

 constructor() : this("", null, null, null)
}
