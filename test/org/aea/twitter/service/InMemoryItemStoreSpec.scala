package org.aea.twitter.service

import org.scalatest.{FunSpec, Matchers, Succeeded}

class InMemoryItemStoreSpec extends FunSpec with Matchers {

  val items = List("a", "b", "c", "d", "a", "b", "e", "c", "a", "b", "a")
  describe ("InMemoryStore ") {

//    it ("should keep top 2") {
//      val store = new InMemoryItemStore[String](20, 2)
//      items.foreach(store.addItem(_))
//      val topItems = store.topItems
//      topItems.size shouldBe 2
//      topItems should contain("a")
//      topItems should contain("b")
//    }
    it ("should only keep last max recent items") {
      val store = new InMemoryItemStore[String](4, 2)
      items.foreach(store.addItem(_)) // add more unique items than will fit in memory
      val topItems = store.topItems
      println(s"items: ${store.getAll}")
      println(s"top: ${store.getTop}")
      topItems.size shouldBe 2
      store.getAll.size shouldBe 4
      topItems should contain("a")
      topItems should contain("b")
    }
  }
}

