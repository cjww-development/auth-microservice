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
import models.UserAccount
import play.api.Logger
import reactivemongo.bson.BSONDocument

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

sealed trait Use
case object UserNameInUse extends Use
case object UserNameNotInUse extends Use

case object EmailInUse extends Use
case object EmailNotInUse extends Use

@Singleton
class RegistrationRepository @Inject()(mongoConnector : MongoConnector) extends MongoCollections{

  def insertNewUser(user : UserAccount) : Future[MongoResponse] = {
    mongoConnector.create[UserAccount](USER_ACCOUNTS, user)
  }

  def verifyUserName(username : String) : Future[Use] = {
    mongoConnector.read[UserAccount](USER_ACCOUNTS, BSONDocument("userName" -> username)) map {
      case MongoSuccessRead(_)  => Logger.info(s"[RegistrationRepository] - [verifyUserName] : This user name is already in use on this system")
        UserNameInUse
      case MongoFailedRead            => UserNameNotInUse
      case _ => throw new IllegalStateException
    }
  }

  def verifyEmail(email : String) : Future[Use] = {
    mongoConnector.read[UserAccount](USER_ACCOUNTS, BSONDocument("email" -> email)) map {
      case MongoSuccessRead(_) => Logger.info(s"[RegistrationRepository] - [verifyEmail] : This email address is already in use on this system")
        EmailInUse
      case MongoFailedRead => EmailNotInUse
      case _ => throw new IllegalStateException
    }
  }
}
