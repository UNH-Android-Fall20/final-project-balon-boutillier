package dev.project.ib2d2.Models

import java.io.Serializable

data class Backup(
    val default: String = ""
) : Serializable {
    val createdBy: String = ""
    val fileName: String = ""
    val shaHash: String = ""
    val timeStamp: String = ""
    val title: String = ""
    val desc: String = ""
}