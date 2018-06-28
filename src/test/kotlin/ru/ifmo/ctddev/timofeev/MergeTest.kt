package ru.ifmo.ctddev.timofeev

import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.*

class MergeTest {
  @Test
  fun testStupidMerge() {
    (1..1).forEach { count ->
      println("count = $count")
      val parts = generateSorted()
      val actual = stupidMerge(parts)
      val expected = parts.flatMap { it.asIterable() }.sorted().toIntArray()
      assertTrue(expected.contentEquals(actual))
    }
  }

  @Test
  fun testHeapMerge() {
    (1..5).forEach { count ->
      println("count = $count")
      val parts = generateSorted()
      val actual = heapMerge(parts)
      val expected = parts.flatMap { it.asIterable() }.sorted().toIntArray()
      assertTrue(expected.contentEquals(actual))
    }
  }

  fun generateSorted(n: Int = 200, arraySize: Int = 10_000): List<IntArray> {
    val random = Random()
    return (1..n).map { IntArray(arraySize) { random.nextInt() }.sortedArray() }
  }
}