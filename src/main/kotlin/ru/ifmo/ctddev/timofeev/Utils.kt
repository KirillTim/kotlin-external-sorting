package ru.ifmo.ctddev.timofeev

import ru.ifmo.ctddev.timofeev.io.FileWriter
import java.nio.file.Path
import java.util.*

const val ONE_KB = 1024

const val ONE_MB = 1024 * ONE_KB

const val ONE_GB = 1024 * ONE_MB

fun <T : Comparable<T>> Iterable<T>.isSorted(): Boolean = iterator().isSorted()

fun IntArray.isSorted(): Boolean = iterator().isSorted()

fun <T : Comparable<T>> Iterator<T>.isSorted(): Boolean {
  if (!hasNext()) return true
  var prv = next()
  while (hasNext()) {
    val cur = next()
    if (prv <= cur) prv = cur
    else return false
  }
  return true
}



fun generateFile(path: Path, size: Int) {
  require(size % 4 == 0)
  val random = Random()
  val writer = FileWriter(path)
  val count = size / 4
  for (i in 0 until count) {
    writer.writeInt(random.nextInt())
  }
  writer.close()
}