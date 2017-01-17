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

import play.api.mvc.Result
import play.api.mvc.Results._
import repositories._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object ValidationService extends ValidationService {
  val regRepo = RegistrationRepository
}

trait ValidationService {
  val regRepo : RegistrationRepository

  def isUserNameInUse(username : String) : Future[Result] = {
    regRepo.verifyUserName(username) map {
      case UserNameNotInUse => Ok
      case UserNameInUse => Conflict
    }
  }

  def isEmailInUse(email : String) : Future[Result] = {
    regRepo.verifyEmail(email) map {
      case EmailNotInUse => Ok
      case EmailInUse => Conflict
    }
  }
}