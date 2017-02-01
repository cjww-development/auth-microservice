// Copyright (C) 2016-2017 the original author or authors.
// See the LICENCE.txt file distributed with this work for additional
// information regarding copyright ownership.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package connectors

import com.google.inject.{Inject, Singleton}
import config._
import play.api.Logger
import play.api.libs.json.OFormat
import reactivemongo.api.MongoConnection.ParsedURI
import reactivemongo.api.{DefaultDB, MongoConnection, MongoDriver}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class MongoConnector @Inject()() extends ConfigurationStrings {

  val driver = new MongoDriver()
  val mongoUri : ParsedURI = MongoConnection.parseURI(databaseUri).get
  val connection: MongoConnection = driver.connection(mongoUri)
  val database: Future[DefaultDB] = connection.database(mongoUri.db.get)
  def collection(collectionName : String) : Future[JSONCollection] = database map {
    _.collection(collectionName)
  }

  def create[T](collectionName : String, data : T)(implicit format : OFormat[T]) : Future[MongoResponse] = {
    collection(collectionName) flatMap {
      _.insert[T](data) map { res =>
        if(res.ok) {
          MongoSuccessCreate
        } else {
          Logger.error(s"[MongoConnector] - [create] : Inserting document of type ${data.getClass} FAILED reason : ${res.errmsg.get}")
          MongoFailedCreate
        }
      }
    }
  }

  def read[T](collectionName : String, query : BSONDocument)(implicit format : OFormat[T]) : Future[MongoResponse] = {
    collection(collectionName) flatMap {
      _.find[BSONDocument](query).one[T] map {
        case Some(data) => MongoSuccessRead(data)
        case None =>
          Logger.info(s"[MongoConnector] - [read] : Query returned no results")
          MongoFailedRead
      }
    }
  }

  def update(collectionName : String, selectedData : BSONDocument, data : BSONDocument) : Future[MongoResponse] = {
    collection(collectionName) flatMap {
      _.update(selectedData, data) map { res =>
        if(res.ok) {
          MongoSuccessUpdate
        } else {
          Logger.error(s"[MongoConnector] - [update] : Updating a document in $collectionName FAILED reason : ${res.errmsg.get}")
          MongoFailedUpdate
        }
      }
    }
  }

  def delete[T](collectionName : String, query : BSONDocument) : Future[MongoResponse] = {
    collection(collectionName) flatMap {
      _.remove(query) map { res =>
        if(res.ok) {
          MongoSuccessDelete
        } else {
          Logger.error(s"[MongoConnector] - [delete] : Deleting a document from $collectionName FAILED reason : ${res.errmsg.get}")
          MongoFailedDelete
        }
      }
    }
  }
}
