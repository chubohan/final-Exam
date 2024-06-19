package imd.ntub.mybottonnav

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: Int,
    val name: String,
    val phone: String
) : Parcelable