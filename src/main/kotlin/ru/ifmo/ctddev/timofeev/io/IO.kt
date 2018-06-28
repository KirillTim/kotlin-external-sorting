package ru.ifmo.ctddev.timofeev.io

import ru.ifmo.ctddev.timofeev.ONE_KB
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.system.measureTimeMillis

data class IOStat(var counts: Int = 0, var totalTimeMs: Long = 0)

class FileReadIterator(val path: Path, val bufferSize: Int = 8 * ONE_KB) : Iterator<Int> {
  init {
    require(bufferSize >= 4)
  }

  val stat = IOStat()
  private val channel = FileChannel.open(path, StandardOpenOption.READ)
  private val buffer = ByteBuffer.allocateDirect(bufferSize)
  var readsCount = 0
    private set

  override fun hasNext(): Boolean {
    if (readsCount == 0 || buffer.remaining() == 0) {
      buffer.clear()
      var sz = -1
      stat.totalTimeMs += measureTimeMillis {
        sz = channel.read(buffer)
        buffer!!.flip()
      }
      stat.counts++
      if (sz <= 0) {
        channel.close()
        return false
      }
      readsCount++
    }
    return buffer.remaining() > 0
  }

  override fun next(): Int {
    return buffer.int
  }
}

class FileWriter(val file: Path, val bufferSize: Int = 8 * ONE_KB) {

  val stat = IOStat()
  private val channel = FileChannel.open(file, StandardOpenOption.WRITE, StandardOpenOption.CREATE)
  private val buffer = ByteBuffer.allocateDirect(bufferSize)

  fun writeInt(value: Int) {
    if (!buffer.hasRemaining()) {
      flush()
      buffer.clear()
    }
    buffer.putInt(value)
  }

  fun flush() {
    stat.totalTimeMs += measureTimeMillis {
      buffer.flip()
      while (buffer.hasRemaining()) {
        channel.write(buffer)
      }
    }
    stat.counts++
  }

  fun close() {
    if (buffer.position() > 0) flush()
    channel.close()
  }
}