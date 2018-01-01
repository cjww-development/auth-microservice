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

import javax.inject.Inject

import com.cjwwdev.reactivemongo.MongoDatabase
import models.{Login, UserAccount}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LoginRepositoryImpl @Inject extends LoginRepository

trait LoginRepository extends MongoDatabase {
  private def query(userDetails: Login): BSONDocument = BSONDocument("userName" -> userDetails.username, "password" -> userDetails.password)

  def validateIndividualUser(userdetails : Login) : Future[Option[UserAccount]] = {
    collection flatMap {
      _.find(query(userdetails)).one[UserAccount]
    }
  }
}
