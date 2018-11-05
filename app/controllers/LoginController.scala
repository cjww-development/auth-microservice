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
package controllers

import com.cjwwdev.config.ConfigurationLoader
import com.cjwwdev.implicits.ImplicitDataSecurity._
import common.BackendController
import javax.inject.Inject
import models.Login
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.LoginService

import scala.concurrent.ExecutionContext.Implicits.global

class DefaultLoginController @Inject()(val loginService: LoginService,
                                       val config: ConfigurationLoader,
                                       val controllerComponents: ControllerComponents) extends LoginController {
  override val appId: String = config.getServiceId(config.get[String]("appName"))
}

trait LoginController extends BackendController {
  val loginService: LoginService

  def login(enc: String) : Action[AnyContent] = Action.async { implicit request =>
    applicationVerification {
      withEncryptedUrl[Login](enc) { creds =>
        loginService.login(creds) map { context =>
          val (status, body) = context.fold((FORBIDDEN, "User could not be authenticated"))(ctx => (OK, ctx.encrypt))
          withJsonResponseBody(status, body) { json =>
            status match {
              case OK        => Ok(json)
              case FORBIDDEN => Forbidden(json)
            }
          }
        }
      }
    }
  }

  def getCurrentUser(contextId: String) : Action[AnyContent] = Action.async { implicit request =>
    applicationVerification {
      validateAs(CONTEXT, contextId) {
        loginService.getContext(contextId) map { context =>
          val (status, body) = context.fold((NOT_FOUND, "No current user found"))(context => (OK, context.encrypt))

          withJsonResponseBody(status, body) { json =>
            status match {
              case OK        => Ok(json)
              case NOT_FOUND => NotFound(json)
            }
          }
        }
      }
    }
  }
}
