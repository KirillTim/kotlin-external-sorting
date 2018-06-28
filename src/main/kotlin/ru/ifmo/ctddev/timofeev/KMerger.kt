package ru.ifmo.ctddev.timofeev

import ru.ifmo.ctddev.timofeev.io.FileReadIterator
import ru.ifmo.ctddev.timofeev.io.FileWriter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.system.measureTimeMillis

class KMerger(private val partsCount: Int, private val tempDirectory: Path) {

  var totalReadTimeMs = 0L
    private set
  var totalWriteTimeMs = 0L
    private set

  fun merge(parts: List<Path>, outputFile: Path) {
    val totalMergeTime = measureTimeMillis {
      val result = realMerge(parts)
      Files.move(result, outputFile, StandardCopyOption.REPLACE_EXISTING)
    }
    println("totalMergeTime = $totalMergeTime")
    println("totalReadTimeMs = $totalReadTimeMs, totalWriteTimeMs = $totalWriteTimeMs")
  }

  private fun realMerge(allParts: List<Path>): Path {
    var parts = allParts
    while (parts.size > 1) {
      val newParts = sortParts(parts)
      parts = newParts
    }
    return parts.single()
  }

  private fun sortParts(parts: List<Path>): List<Path> {
    println("sortParts(parts.size = ${parts.size})")
    val newParts = arrayListOf<Path>()
    var pos = 0
    while (pos < parts.size) {
      val subParts = parts.subList(pos, minOf(parts.size, pos + partsCount))
      val tempTarget = createTempTarget()
      mergeFiles(subParts, tempTarget)
      newParts.add(tempTarget)
      pos += partsCount
    }
    return newParts
  }

  private fun createTempTarget() = Files.createTempFile(tempDirectory, "merge", "")

  private fun mergeFiles(parts: List<Path>, target: Path) {
    require(parts.size <= partsCount)
    if (parts.size == 1) {
      Files.move(parts.single(), target, StandardCopyOption.REPLACE_EXISTING)
      return
    }

    val readers = parts.map { FileReadIterator(it) }
    val writer = FileWriter(target)
    val mergeIterator = MergeIterator(readers)
    while (mergeIterator.hasNext()) {
      writer.writeInt(mergeIterator.next())
    }
    totalReadTimeMs += readers.map { it.stat.totalTimeMs }.sum()
    totalWriteTimeMs += writer.stat.totalTimeMs
    writer.close()
  }
}
