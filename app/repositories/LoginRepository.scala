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
package repositories

import com.google.inject.{Inject, Singleton}
import config.{MongoCollections, MongoFailedRead, MongoResponse, MongoSuccessRead}
import connectors.MongoConnector
import models.{AuthContext, Login, UserAccount}
import reactivemongo.bson.BSONDocument

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class LoginRepository @Inject()(mongoConnector: MongoConnector) extends MongoCollections {

  def validateIndividualUser(userdetails : Login) : Future[Option[UserAccount]] = {
    val query = BSONDocument("userName" -> userdetails.username, "password" -> userdetails.password)
    mongoConnector.read[UserAccount](USER_ACCOUNTS, query) map {
      case MongoFailedRead => None
      case MongoSuccessRead(acc) => Some(acc.asInstanceOf[UserAccount])
      case _ => throw new IllegalStateException
    }
  }

  def cacheContext(context: AuthContext) : Future[MongoResponse] = {
    mongoConnector.create[AuthContext](AUTH, context)
  }

  def fetchContext(id : String) : Future[MongoResponse] = {
    mongoConnector.read[AuthContext](AUTH, BSONDocument("_id" -> id))
  }
}
