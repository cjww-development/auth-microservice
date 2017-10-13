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

import java.util.UUID

import com.cjwwdev.auth.models.{AuthContext, User}
import helpers.CJWWSpec
import models.Login
import play.api.test.FakeRequest
import play.api.test.Helpers._
import com.cjwwdev.security.encryption.DataSecurity
import org.joda.time.{DateTime, DateTimeZone}
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers
import play.api.libs.json.JsSuccess

import scala.concurrent.Future

class LoginControllerSpec extends CJWWSpec {

  val testCredentials = Login("testUserName","testPass")

  val encTestCredentials = DataSecurity.encryptType[Login](testCredentials)

  final val now = new DateTime(DateTimeZone.UTC)
  final val uuid = UUID.randomUUID

  private val testContext = AuthContext(
    contextId = s"context-$uuid",
    user = User(
      id = "user-test-user-id",
      firstName = Some("testFirstName"),
      lastName = Some("testLastName"),
      orgName = None,
      "individual",
      None
    ),
    basicDetailsUri = "/test/uri",
    enrolmentsUri = "/test/uri",
    settingsUri = "/test/uri",
    createdAt = now
  )

  val encTestContext = DataSecurity.encryptType[AuthContext](testContext)

  class Setup {
    val testController = new LoginController(mockLoginService, mockConfig)
  }

  "login" should {
    "return an OK" when {
      "the user has been successfully validated and the body should contain an encrypted auth context" in new Setup {
        when(mockLoginService.login(ArgumentMatchers.eq(testCredentials)))
          .thenReturn(Future.successful(Some(testContext)))

        val result = testController.login(encTestCredentials)(FakeRequest().withHeaders("appId" -> AUTH_SERVICE_ID))
        status(result) mustBe OK
        contentAsString(result) mustBe encTestContext
        DataSecurity.decryptIntoType[AuthContext](contentAsString(result)) mustBe JsSuccess(testContext)
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

      val result = testController.getContext(s"context-$uuid")(FakeRequest().withHeaders("appId" -> AUTH_SERVICE_ID))
      status(result) mustBe OK
    }

    "return a NOT_FOUND" in new Setup {
      when(mockLoginService.getContext(ArgumentMatchers.any()))
        .thenReturn(Future.successful(None))

      val result = testController.getContext(s"context-$uuid")(FakeRequest().withHeaders("appId" -> AUTH_SERVICE_ID))
      status(result) mustBe NOT_FOUND
    }

    "return a FORBIDDEN" in new Setup {
      val result = testController.getContext("context-test-id-12345")(FakeRequest())
      status(result) mustBe FORBIDDEN
    }
  }
}
