package ru.ifmo.ctddev.timofeev.io

import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
  /*val stream = DataOutputStream(FileOutputStream("data"))
  (1..1024 * 1024).forEach {
    stream.writeInt(it)
  }*/
  val iterator = FileReadIterator(File("data").toPath(), 400000)
  var total = 0
  val timeFast = measureTimeMillis {
    while (iterator.hasNext()) {
      iterator.next()
      total++
    }
  }

  println("timeFast = $timeFast ms, total ints = $total")

  val inputStream = DataInputStream(FileInputStream("data"))
  total = 0
  val timeSlow = measureTimeMillis {
    while (true) {
      try {
        val data = inputStream.readInt()
        total++
        //println(data)
      } catch (e: Exception) {
        break
      }
    }
  }

  println("timeSlow = $timeSlow ms, total ints = $total")
}