package lib.vector

import java.util.concurrent.atomic.AtomicLong
import kotlin.random.Random
import kotlin.random.nextULong

private val nextPointId: AtomicLong = AtomicLong(0)
fun getNextPointId(name: String? = null): Id {
  return Id(if(name != null) -1 else Random.nextLong() ushr 1 /*nextPointId.getAndIncrement()*/, name = name)
}

