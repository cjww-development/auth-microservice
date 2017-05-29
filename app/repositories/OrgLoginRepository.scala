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

import com.cjwwdev.reactivemongo.{MongoConnector, MongoRepository}
import models.{Login, OrgAccount}
import reactivemongo.api.DB
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class OrgLoginRepository extends MongoConnector {
  val store = new OrgLoginRepo(db)
}

class OrgLoginRepo(db: () => DB) extends MongoRepository("org-accounts", db) {
  def validateOrganisationUser(userDetails: Login): Future[Option[OrgAccount]] = {
    val query = BSONDocument("orgUserName" -> userDetails.username, "password" -> userDetails.password)
    collection.find(query).one[OrgAccount]
  }
}
