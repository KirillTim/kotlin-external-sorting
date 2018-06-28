package ru.ifmo.ctddev.timofeev

import ru.ifmo.ctddev.timofeev.io.FileReadIterator
import ru.ifmo.ctddev.timofeev.io.FileWriter
import java.io.File
import java.nio.file.Path
import java.util.*
import kotlin.system.measureTimeMillis

fun sortAndSaveParts(input: Path, availableMemory: Int, distDir: Path? = null): List<Path> {
  val parts = arrayListOf<Path>()
  val array = IntArray(availableMemory / 4)
  var index = 0
  val reader = FileReadIterator(input)
  while (reader.hasNext()) {
    if (index == array.size) {
      parts.add(sortPart(array, index, parts.size, input, distDir))
      index = 0
    } else {
      array[index] = reader.next()
      index++
    }
  }
  if (index > 0) {
    parts.add(sortPart(array, index, parts.size, input, distDir))
  }
  return parts
}

private fun sortPart(array: IntArray, size: Int, partIndex: Int, input: Path, distDir: Path?): Path {
  val partName = "${input.fileName}_sorted_${partIndex}__"
  val outFile =
    if (distDir != null) File.createTempFile(partName, "", distDir.toFile())
    else File.createTempFile(partName, "", input.parent.toFile())
  val outPath = outFile.toPath()
  Arrays.parallelSort(array, 0, size)
  val writer = FileWriter(outPath)
  for (i in 0 until size) {
    writer.writeInt(array[i])
  }
  return outPath
}

fun main(args: Array<String>) {
  val inputPath = File("data").toPath()
  val partsDir = File("tmp-parts").apply { mkdir() }
  val writer = FileWriter(inputPath)
  val random = Random()
  val list = (1..ONE_MB * 123).map { random.nextInt() }
  val totalTimeMs = measureTimeMillis {
    list.forEach { writer.writeInt(it) }
    writer.close()
  }

  println("total write time: $totalTimeMs, writer stat: ${writer.stat}")

  val parts = sortAndSaveParts(inputPath, 10 * ONE_MB, partsDir.toPath())
  KMerger(10, File("tmp-merges").apply { mkdir() }.toPath()).merge(parts, File("merged-data").toPath())
  //val sortedParts = parts.map { FileReadIterator(it).asSequence().toList() }
  //assert(sortedParts.all { it.isSorted() })

  //assert(list.sorted() == MergeIterator(sortedParts.map { it.iterator() }).asSequence().toList())

  //assert(list.sorted().toIntArray().contentEquals(heapMerge(sortedParts)))
}
