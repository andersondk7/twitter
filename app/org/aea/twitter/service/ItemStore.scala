package org.aea.twitter.service

/**
  * Represents storage of items from sampled tweets
  * @tparam T type of item stored
  */
trait ItemStore[T] {

  /**
    * Reset the store
    */
  def reset: Unit

  /**
    * Add an item to the store
    * @param item item to be stored
    */
  def addItem(item: T): Unit

  /**
    * Return the items added most frequently
    * @return most frequent items
    */
  def topItems: Seq[T]
}


