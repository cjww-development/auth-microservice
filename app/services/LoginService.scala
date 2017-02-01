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

import com.google.inject.{Inject, Singleton}
import config._
import models.{AuthContext, AuthContextDetail, Login, UserAccount}
import play.api.Logger
import play.api.mvc.Result
import play.api.mvc.Results._
import repositories.LoginRepository
import utils.security.DataSecurity

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class LoginService @Inject()(loginRepository: LoginRepository) {

  def login(credentials : Login) : Future[Result] = {
    loginRepository.validateIndividualUser(credentials) flatMap {
      case Some(account) =>
        processAuthContext(account) map {
          case Some(detail) =>
            DataSecurity.encryptData[AuthContextDetail](detail) match {
              case Some(data) => Ok(data)
              case None => InternalServerError
            }
          case None => Forbidden
        }
      case None => Future.successful(Forbidden)
    }
  }

  def getContext(id : String) : Future[Result] = {
    loginRepository.fetchContext(id) map {
      case MongoFailedRead => NotFound
      case MongoSuccessRead(context) =>
        Logger.debug(s"CONTEXT : $context")
        DataSecurity.encryptData[AuthContext](context.asInstanceOf[AuthContext]) match {
          case Some(data) =>
            Logger.debug(s"GET CONTEXT: $context")
            Ok(data)
          case None => InternalServerError
        }
      case _ => throw new IllegalStateException
    }
  }

  def processAuthContext(acc : UserAccount) : Future[Option[AuthContextDetail]] = {
    val generatedAuthContext = AuthContext.generate(acc._id.get, acc.firstName, acc.lastName)
    loginRepository.cacheContext(generatedAuthContext) map {
      case MongoSuccessCreate => Some(AuthContextDetail.build(generatedAuthContext._id, acc))
      case MongoFailedCreate => None
      case _ => throw new IllegalStateException
    }
  }
}
