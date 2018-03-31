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

import com.cjwwdev.auth.models.CurrentUser
import com.cjwwdev.logging.Logging
import com.cjwwdev.mongo.DatabaseRepository
import com.cjwwdev.mongo.connection.ConnectionSettings
import com.cjwwdev.mongo.responses.{MongoCreateResponse, MongoFailedCreate, MongoSuccessCreate}
import common.CurrentUserNotFoundException
import javax.inject.Inject
import play.api.Configuration
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ContextRepositoryImpl @Inject()(val config: Configuration) extends ContextRepository with ConnectionSettings

trait ContextRepository extends DatabaseRepository with Logging {
  override def indexes: Seq[Index] = Seq(
    Index(
      key    = Seq("contextId" -> IndexType.Ascending),
      name   = Some("ContextId"),
      unique = true,
      sparse = false
    )
  )

  private val contextIdSelector: String => BSONDocument = contextId => BSONDocument("contextId" -> contextId)

  def cacheCurrentUser(context: CurrentUser) : Future[MongoCreateResponse] = {
    for {
      col <- collection
      wr  <- col.insert[CurrentUser](context)
    } yield if(wr.ok) {
      logger.info(s"[cacheCurrentUser] current user ${context.contextId} has been created")
      MongoSuccessCreate
    } else {
      logger.error(s"[cacheCurrentUser] current user ${context.contextId} has not created")
      MongoFailedCreate
    }
  }

  def fetchCurrentUser(contextId : String) : Future[CurrentUser] = {
    for {
      col <- collection
      res <- col.find(contextIdSelector(contextId)).one[CurrentUser]
    } yield res.getOrElse(throw new CurrentUserNotFoundException(s"AuthContext not found for context id $contextId"))
  }
}
