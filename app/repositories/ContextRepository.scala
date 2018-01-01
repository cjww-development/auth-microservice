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

import com.cjwwdev.auth.models.AuthContext
import com.cjwwdev.reactivemongo._
import common.AuthContextNotFoundException
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ContextRepositoryImpl @Inject extends ContextRepository

trait ContextRepository extends MongoDatabase {
  override def indexes: Seq[Index] = Seq(
    Index(
      key    = Seq("contextId" -> IndexType.Ascending),
      name   = Some("ContextId"),
      unique = true,
      sparse = false
    )
  )

  private val contextIdSelector: String => BSONDocument = contextId => BSONDocument("contextId" -> contextId)

  def cacheContext(context: AuthContext) : Future[MongoCreateResponse] = {
    for {
      col <- collection
      wr  <- col.insert[AuthContext](context)
    } yield if(wr.ok) {
      logger.info(s"[ContextRepo] - [cacheContext] context ${context.contextId} has been created")
      MongoSuccessCreate
    } else {
      logger.error(s"[ContextRepo] - [cacheContext] context ${context.contextId} has not created")
      MongoFailedCreate
    }
  }

  def fetchContext(contextId : String) : Future[AuthContext] = {
    for {
      col <- collection
      res <- col.find(contextIdSelector(contextId)).one[AuthContext]
    } yield res.getOrElse(throw new AuthContextNotFoundException(s"AuthContext not found for context id $contextId"))
  }
}
