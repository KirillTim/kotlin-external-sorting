package ru.ifmo.ctddev.timofeev

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.system.measureTimeMillis

object ExternalSorter {
  fun sort(input: Path, output: Path, maxMemory: Int, mergeKParts: Int) {
    require(mergeKParts >= 2)

    val tmpSortDir = Files.createTempDirectory("sort-tmp-dir").apply { toFile().deleteOnExit() }
    val tmpMergeDir = Files.createTempDirectory("merge-tmp-dir").apply { toFile().deleteOnExit() }
    println("input: $input, tmpDir = $tmpSortDir")
    val sortedParts = mutableListOf<Path>()
    val sortTimeMs = measureTimeMillis {
      sortedParts += sortAndSaveParts(input, maxMemory, tmpSortDir)
    }
    println("spent $sortTimeMs ms for sorting and saving ${sortedParts.size} parts")

    if (sortedParts.size == 1) {
      println("No merge happend")
      Files.move(sortedParts.single(), output, StandardCopyOption.REPLACE_EXISTING)
    }
    val mergeTimeMs = measureTimeMillis {
      KMerger(mergeKParts, tmpMergeDir).merge(sortedParts, output)
    }
    println("merge $mergeKParts parts at once, spent $mergeTimeMs ms")
  }
}