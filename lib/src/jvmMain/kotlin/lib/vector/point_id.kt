package lib.vector

import java.util.concurrent.atomic.AtomicLong

private val nextPointId: AtomicLong = AtomicLong(0)
fun getNextPointId(name: String? = null): Id {
  return Id(if(name != null) -1 else nextPointId.getAndIncrement(), name = name)
}

