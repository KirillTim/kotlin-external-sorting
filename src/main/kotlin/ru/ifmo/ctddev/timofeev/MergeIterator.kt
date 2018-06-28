package ru.ifmo.ctddev.timofeev

import java.util.*

class MergeIterator<T : Comparable<T>>(iterators: List<Iterator<T>>) : Iterator<T> {

  private class Wrapper<T : Comparable<T>>(var head: T, var iterator: Iterator<T>) : Comparable<Wrapper<T>> {
    override fun compareTo(other: Wrapper<T>): Int {
      val v = head.compareTo(other.head)
      return if (v == 0) iterator.hashCode().compareTo(other.iterator.hashCode()) else v //WTF?
    }
  }

  private val heap = PriorityQueue<Wrapper<T>>(iterators.filter { it.hasNext() }.map { Wrapper(it.next(), it) })

  override fun hasNext(): Boolean {
    return heap.isNotEmpty()
  }

  override fun next(): T {
    val top = heap.poll()
    with(top.iterator) {
      if (hasNext()) heap.add(Wrapper(next(), this))
    }
    return top.head
  }
}