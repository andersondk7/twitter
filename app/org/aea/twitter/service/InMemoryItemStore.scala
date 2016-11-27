package org.aea.twitter.service

import scala.collection.mutable
import InMemoryItemStore._

import scala.collection.mutable.ListBuffer

/**
  * InMemory implementation of an item store
  * <p>
  *   In order to keep memory from exploding as an large number of unique items are added,
  *   once the <tt>maxMemoryCount</tt> is reached older entries are removed from memory. </b>
  *   While this keeps an upper bound on the memory, it will 'forget' old entries once
  *   the <tt>maxMemoryCount</tt> has been reached.
  * </p>
  * <p>
  *   In practice this means that if an item is entered infrequently (that is there are other unique
  *   <tt>MaxMemoryCount</tt> items added between adding the infrequent item, the infequent item
  *   will only be 'counted' once and will not be included in the top items list.
  * </p>
  * <p>
  *   This is <b>Not</b> thread safe and is intended to only have one instance per actor,
  *   that is <b>not shared</b> between actors or threads
  * </p>
  * @param maxMemoryCount
  * @param mostPopularCount
  * @tparam T
  */
class InMemoryItemStore[T](maxMemoryCount: Int = MAX_SIZE, mostPopularCount: Int = MOST_POPULAR) extends ItemStore[T] {

  protected[service] case class Count(count: Long, item: T) extends Ordered[Count] {
    override def compare(that: Count): Int = that.count compare this.count
  }

  private val allItems = new mutable.ListBuffer[Count]
  private var top = new mutable.ListBuffer[Count]()

  override def reset: Unit = {
    allItems.clear()
    top.clear()
  }

  override def addItem(item: T): Unit = {
    val update: (Int, Count) = allItems.indexWhere(_.item == item) match {
      case n if n < 0 => (-1, Count(1, item))
      case i =>
        val current = allItems(i)
        (i, current.copy(count = current.count + 1 ))
    }
    if (update._1 == -1) allItems += update._2
    else  allItems(update._1) = update._2

    if (allItems.size > maxMemoryCount) allItems.indexWhere(_.count == 1) match {
      case n if n > -1 => allItems.remove(n)

      // this is a bug, if there were no items with a count of 1, this will remove the first item,
      // not the item with the smallest count
      //
      // for proof of concept this may be sufficient, but for real use, a true datastore should be used instead
      case _ => allItems.remove(0)
    }

    val updateList: ListBuffer[Count] = top.filterNot(_.item == item)
    top = (updateList += update._2 ).sorted.take(mostPopularCount)
  }

  override def topItems: Seq[T] = top.map(_.item)

  protected[service] def getAll:List[Count] = allItems.toList

  protected[service] def getTop:List[Count] = top.toList
}

object InMemoryItemStore {
  val MOST_POPULAR: Int = 10
  val MAX_SIZE: Int = 100000
}
