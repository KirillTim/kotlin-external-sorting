package ru.ifmo.ctddev.timofeev

import ru.ifmo.ctddev.timofeev.io.FileWriter
import java.io.File

fun main(args: Array<String>) {
  val writer = FileWriter(File("data").toPath())
  val list = (1..1024 * 1024 * 2).shuffled()
  list.forEach { writer.writeInt(it) }
  writer.close()
}