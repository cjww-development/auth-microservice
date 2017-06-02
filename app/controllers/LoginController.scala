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

import com.cjwwdev.auth.actions.{Authorised, BaseAuth, NotAuthorised}
import com.cjwwdev.auth.models.AuthContext
import com.cjwwdev.logging.Logger
import com.cjwwdev.request.RequestParsers
import com.cjwwdev.security.encryption.DataSecurity
import com.google.inject.{Inject, Singleton}
import models.Login
import play.api.mvc.{Action, AnyContent, Controller}
import services.LoginService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class LoginController @Inject()(loginService : LoginService) extends Controller with RequestParsers with BaseAuth {
  def login(enc : String) : Action[AnyContent] = Action.async {
    implicit request =>
      openActionVerification {
        case Authorised =>
          decryptUrlIntoType[Login](enc)(Login.standardFormat) { creds =>
            loginService.login(creds) map {
              case Some(context) => Ok(DataSecurity.encryptType[AuthContext](context).get)
              case None => Forbidden
            }
          }
        case NotAuthorised => Future.successful(Forbidden)
      }
  }

  def getContext(contextId : String) : Action[AnyContent] = Action.async {
    implicit request =>
      openActionVerification {
        case Authorised => loginService.getContext(contextId) map {
          case Some(context) => Ok(DataSecurity.encryptType[AuthContext](context).get)
          case None => NotFound
        }
        case NotAuthorised => Future.successful(Forbidden)
      }
  }
}
