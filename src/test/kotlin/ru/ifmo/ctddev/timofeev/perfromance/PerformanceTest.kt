package ru.ifmo.ctddev.timofeev.perfromance

import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import ru.ifmo.ctddev.timofeev.ExternalSorter
import ru.ifmo.ctddev.timofeev.ONE_MB
import ru.ifmo.ctddev.timofeev.generateFile
import ru.ifmo.ctddev.timofeev.io.FileReadIterator
import ru.ifmo.ctddev.timofeev.isSorted
import java.io.File
import java.nio.file.Path
import kotlin.system.measureTimeMillis

const val fileSize = 512 * ONE_MB

const val maxMemory = ONE_MB * 8

class PerformanceTest {

  @Rule
  @JvmField
  val tempFolder = TemporaryFolder()

  val outputFile: Path by lazy { tempFolder.newFile().toPath() }

  val inputDir = File(File("testData"), "input")
  val outputDir = File(File("testData"), "output")

  val inputData = File(inputDir, "data")
  val resultPath = File(outputDir, "result").toPath()

  @Before
  fun before() {
    if (!inputData.exists() && inputData.length() != fileSize.toLong()) {
      println("generate file of size $fileSize into ${inputData.absolutePath}")
      generateFile(inputData.toPath(), fileSize)
    }
  }

  @Test
  fun maxMemorySpeedTest() {
    //val memory = listOf(4, 8, 16, 32, 64, 128, 256, 512, 1024).map { it * ONE_MB }
    val memory = listOf(512 * ONE_MB)
    memory.forEach { maxMem ->
      val timeSpent = measureTimeMillis {
        ExternalSorter.sort(inputData.toPath(), resultPath, maxMem, 1024)
      }
      println("mergePartsNumberSpeedTest: k=1024, maxMemory=$maxMemory, timeSpent=$timeSpent")
    }
  }

  @Test
  fun mergePartsNumberSpeedTest() {
    //val parts = listOf(2, 4, 8, 16, 32, 64, 128)
    val parts = listOf(32)
    parts.forEach { part ->
      val timeSpent = measureTimeMillis {
        ExternalSorter.sort(inputData.toPath(), resultPath, maxMemory, part)
      }
      println("mergePartsNumberSpeedTest: k=$part, maxMemory=$maxMemory, timeSpent=$timeSpent")
    }
    Assert.assertTrue(FileReadIterator(resultPath).isSorted())
  }
}