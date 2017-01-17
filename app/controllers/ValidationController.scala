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
package controllers

import play.api.mvc.{Action, AnyContent}
import services.ValidationService
import utils.application.{Authorised, BackendController, NotAuthorised}
import utils.security.DataSecurity

import scala.concurrent.Future

class ValidationController extends ValidationCtrl {
  val validationService = ValidationService
}

trait ValidationCtrl extends BackendController {

  val validationService : ValidationService

  def validateUserName(username : String) : Action[AnyContent] = Action.async {
    implicit request =>
      openActionVerification {
        case Authorised =>
         DataSecurity.decryptInto[String](username) match {
           case Some(decrypted) => validationService.isUserNameInUse(decrypted)
           case None => Future.successful(BadRequest)
         }
        case NotAuthorised => Future.successful(Forbidden)
      }
  }

  def validateEmail(email : String) : Action[AnyContent] = Action.async {
    implicit request =>
      openActionVerification {
        case Authorised =>
          DataSecurity.decryptInto[String](email) match {
            case Some(decrypted) => validationService.isEmailInUse(decrypted)
            case None => Future.successful(BadRequest)
          }
        case NotAuthorised => Future.successful(Forbidden)
      }
  }
}
