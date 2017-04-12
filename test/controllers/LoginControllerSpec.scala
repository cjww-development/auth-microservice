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

import helpers.CJWWSpec
import models.{AuthContext, Login}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.LoginService
import com.cjwwdev.security.encryption.DataSecurity
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers

import scala.concurrent.Future

class LoginControllerSpec extends CJWWSpec {

  val mockLoginService = mock[LoginService]

  val testCredentials = Login("testUserName","testPass")

  val encTestCredentials = DataSecurity.encryptData[Login](testCredentials).get

  val testContext = AuthContext.generate("testUserId", "firstName", "lastName")

  val encTestContext = DataSecurity.encryptData[AuthContext](testContext).get

  class Setup {
    val testController = new LoginController(mockLoginService)
  }

  "login" should {
    "return an OK" when {
      "the user has been successfully validated and the body should contain an encrypted auth context" in new Setup {
        when(mockLoginService.login(ArgumentMatchers.eq(testCredentials)))
          .thenReturn(Future.successful(Some(testContext)))

        val result = testController.login(encTestCredentials)(FakeRequest().withHeaders("appId" -> AUTH_SERVICE_ID))
        status(result) mustBe OK
        contentAsString(result) mustBe encTestContext
        DataSecurity.decryptInto[AuthContext](contentAsString(result)).get mustBe testContext
      }
    }

    "return a FORBIDDEN" when {
      "a request is made with no valid appId" in new Setup {
        val result = testController.login(encTestCredentials)(FakeRequest())
        status(result) mustBe FORBIDDEN
      }

      "a request is made with an invalid payload" in new Setup {
        val result = testController.login("INVALIDPAYLOAD")(FakeRequest().withHeaders("appId" -> AUTH_SERVICE_ID))
        status(result) mustBe BAD_REQUEST
      }

      "the users credentials dont match anything on record" in new Setup {
        when(mockLoginService.login(ArgumentMatchers.eq(testCredentials)))
          .thenReturn(Future.successful(None))

        val result = testController.login(encTestCredentials)(FakeRequest().withHeaders("appId" -> AUTH_SERVICE_ID))
        status(result) mustBe FORBIDDEN
      }
    }
  }

  "getContext" should {
    "return an OK" in new Setup {
      when(mockLoginService.getContext(ArgumentMatchers.any()))
        .thenReturn(Future.successful(Some(testContext)))

      val result = testController.getContext("context-test-id-12345")(FakeRequest().withHeaders("appId" -> AUTH_SERVICE_ID))
      status(result) mustBe OK
    }

    "return a NOT_FOUND" in new Setup {
      when(mockLoginService.getContext(ArgumentMatchers.any()))
        .thenReturn(Future.successful(None))

      val result = testController.getContext("context-test-id-12345")(FakeRequest().withHeaders("appId" -> AUTH_SERVICE_ID))
      status(result) mustBe NOT_FOUND
    }

    "return a FORBIDDEN" in new Setup {
      val result = testController.getContext("context-test-id-12345")(FakeRequest())
      status(result) mustBe FORBIDDEN
    }
  }
}
