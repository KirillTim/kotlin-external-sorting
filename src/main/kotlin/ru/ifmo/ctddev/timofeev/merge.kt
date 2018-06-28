package ru.ifmo.ctddev.timofeev

import java.util.*

private data class KV(val value: Int, val partIndex: Int) : Comparable<KV> {
  override fun compareTo(other: KV): Int {
    val v =  value.compareTo(other.value)
    return if (v == 0) partIndex.compareTo(other.partIndex) else v
  }
}

fun heapMerge(parts: List<IntArray>): IntArray {
  //require(parts.all { it.isNotEmpty() })
  val result = IntArray(parts.sumBy { it.size })
  val heap = PriorityQueue<KV>()
  val nextInput = IntArray(parts.size)
  var outIndex = 0
  parts.forEachIndexed { index, part ->
    part.firstOrNull()?.let { heap.add(KV(it, index)) }
  }
  while (outIndex <= result.lastIndex) {
    val (value, partIndex) = heap.poll()
    nextInput[partIndex]++
    if (nextInput[partIndex] <= parts[partIndex].lastIndex) heap.add(
      KV(
        parts[partIndex][nextInput[partIndex]],
        partIndex
      )
    )
    result[outIndex] = value
    outIndex++
  }
  return result
}

fun stupidMerge(parts: List<IntArray>): IntArray {
  require(parts.all { it.isNotEmpty() })
  val result = IntArray(parts.sumBy { it.size })
  val nextInput = IntArray(parts.size)
  var outIndex = 0
  while (outIndex <= result.lastIndex) {
    var min = Int.MAX_VALUE
    var partIndex = -1
    parts.forEachIndexed { i, part ->
      if (nextInput[i] <= part.lastIndex && part[nextInput[i]] < min) {
        min = part[nextInput[i]]
        partIndex = i
      }
    }
    result[outIndex] = min
    outIndex ++
    nextInput[partIndex] ++
  }
  return result
}