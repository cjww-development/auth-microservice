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
package services

import com.cjwwdev.mongo.{MongoFailedCreate, MongoFailedRead, MongoSuccessCreate, MongoSuccessRead}
import com.google.inject.{Inject, Singleton}
import models.{AuthContext, Login, UserAccount}
import repositories.LoginRepository

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class LoginService @Inject()(loginRepository: LoginRepository) {
  def login(credentials : Login) : Future[Option[AuthContext]] = {
    loginRepository.validateIndividualUser(credentials) flatMap {
      case Some(account) => processAuthContext(account)
      case _ => Future.successful(None)
    }
  }

  def getContext(id : String) : Future[Option[AuthContext]] = {
    loginRepository.fetchContext(id) map {
      case MongoSuccessRead(context) => Some(context.asInstanceOf[AuthContext])
      case MongoFailedRead => None
    }
  }

  private[services] def processAuthContext(acc : UserAccount) : Future[Option[AuthContext]] = {
    val generatedAuthContext = AuthContext.generate(acc._id.get, acc.firstName, acc.lastName)
    loginRepository.cacheContext(generatedAuthContext) map {
      case MongoSuccessCreate => Some(generatedAuthContext)
      case MongoFailedCreate => None
    }
  }
}
