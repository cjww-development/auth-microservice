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
import models.UserAccount
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.RegistrationService
import utils.security.DataSecurity
import org.mockito.Mockito._
import org.mockito.Matchers
import play.api.mvc.Results._

import scala.concurrent.Future

class RegistrationControllerSpec extends CJWWSpec {

  val mockRegistrationService = mock[RegistrationService]

  val user =
    UserAccount(
      None,
      "testFirstName",
      "testLastName",
      "testUserName",
      "test@email.com",
      "testPass",
      None,
      None,
      None
    )

  val encryptedUser = DataSecurity.encryptData[UserAccount](user).get

  class Setup {
    val testController = new RegistrationController(mockRegistrationService)
  }

  "createNewUser" should {
    "return a Created" in new Setup {

      val request =
        FakeRequest()
          .withHeaders("appID" -> AUTH_ID, CONTENT_TYPE -> TEXT)
          .withBody(encryptedUser)

      when(mockRegistrationService.createNewUser(Matchers.eq(user)))
        .thenReturn(Future.successful(Created))

      val result = testController.createNewUser()(request)
      status(result) mustBe CREATED
    }

    "return an internal server error if inserting the user encountered a problem" in new Setup {
      val request =
        FakeRequest()
          .withHeaders(
            "appID" -> AUTH_ID,
            CONTENT_TYPE -> TEXT
          )
          .withBody(encryptedUser)

      when(mockRegistrationService.createNewUser(Matchers.eq(user)))
        .thenReturn(Future.successful(InternalServerError))

      val result = testController.createNewUser()(request)
      status(result) mustBe INTERNAL_SERVER_ERROR
    }
  }
}
