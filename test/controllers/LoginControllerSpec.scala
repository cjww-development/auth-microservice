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

import com.cjwwdev.auth.models.CurrentUser
import com.cjwwdev.implicits.ImplicitDataSecurity._
import com.cjwwdev.implicits.ImplicitJsValues._
import com.cjwwdev.security.obfuscation.{Obfuscation, Obfuscator}
import helpers.controllers.ControllerSpec
import models.Login
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers.stubControllerComponents

class LoginControllerSpec extends ControllerSpec {

  implicit val loginObfuscator: Obfuscator[Login] = new Obfuscator[Login] {
    override def encrypt(value: Login): String = Obfuscation.obfuscateJson(Json.toJson(value))
  }

  val encTestCredentials = testCredentials.encrypt

  class Setup {
    val testController = new LoginController {
      override protected def controllerComponents = stubControllerComponents()
      override val loginService                   = mockLoginService
      override val appId                          = "testAppId"
    }
  }

  "login" should {
    "return an OK" when {
      "the user has been successfully validated and the body should contain an encrypted auth context" in new Setup {
        mockLogin(loggedIn = true)

        runActionWithoutAuth(testController.login(encTestCredentials), standardRequest) { res =>
          status(res)                                                 mustBe OK
          contentAsJson(res).get[String]("body").decrypt[CurrentUser] mustBe Left(testCurrentUser)
        }
      }
    }

    "return a FORBIDDEN" when {
      "a request is made with no valid appId" in new Setup {
        runActionWithoutAuth(testController.login(encTestCredentials), FakeRequest()) {
          status(_) mustBe FORBIDDEN
        }
      }

      "a request is made with an invalid payload" in new Setup {
        runActionWithoutAuth(testController.login("INVALIDPAYLOAD"), standardRequest) {
          status(_) mustBe BAD_REQUEST
        }
      }

      "the users credentials dont match anything on record" in new Setup {
        mockLogin(loggedIn = false)

        runActionWithoutAuth(testController.login(encTestCredentials), standardRequest) {
          status(_) mustBe FORBIDDEN
        }
      }
    }
  }

  "getContext" should {
    "return an OK" in new Setup {
      mockGetContext(fetched = true)

      runActionWithoutAuth(testController.getCurrentUser(generateTestSystemId(CONTEXT)), standardRequest) {
        status(_) mustBe OK
      }
    }

    "return a NOT_FOUND" in new Setup {
      mockGetContext(fetched = false)

      runActionWithoutAuth(testController.getCurrentUser(generateTestSystemId(CONTEXT)), standardRequest) {
        status(_) mustBe NOT_FOUND
      }
    }

    "return a FORBIDDEN" in new Setup {
      runActionWithoutAuth(testController.getCurrentUser(generateTestSystemId(CONTEXT)), FakeRequest()) {
        status(_) mustBe FORBIDDEN
      }
    }
  }
}
