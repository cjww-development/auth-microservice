/*
 * Copyright 2018 CJWW Development
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package repositories

import javax.inject.Inject
import com.cjwwdev.mongo.DatabaseRepository
import com.cjwwdev.mongo.connection.ConnectionSettings
import models.{Login, OrgAccount}
import play.api.Configuration
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class OrgLoginRepositoryImpl @Inject()(val config: Configuration) extends OrgLoginRepository with ConnectionSettings

trait OrgLoginRepository extends DatabaseRepository {
  private def query(userDetails: Login): BSONDocument = BSONDocument("orgUserName" -> userDetails.username, "password" -> userDetails.password)

  def validateOrganisationUser(userDetails: Login): Future[Option[OrgAccount]] = {
    for {
      col <- collection
      acc <- col.find(query(userDetails)).one[OrgAccount]
    } yield acc
  }
}
