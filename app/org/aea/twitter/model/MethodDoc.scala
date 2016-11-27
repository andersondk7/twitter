package org.aea.twitter.model

import play.api.libs.json.{JsValue, Json, Writes}


/**
  * Represents an enpoint method that can be called
  * @param name path to endpoint
  * @param description description of endpont
  */
case class MethodDoc(name: String, description: String)

object MethodDoc {

  implicit val MethodDocWriter = new Writes[MethodDoc] {
    override def writes(doc: MethodDoc): JsValue = Json.obj(
      "name" -> doc.name
      , "description" -> doc.description
    )
  }
}
