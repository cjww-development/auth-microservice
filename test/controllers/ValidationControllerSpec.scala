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
import services.ValidationService
import utils.security.DataSecurity
import org.mockito.Mockito._
import org.mockito.Matchers
import play.api.mvc.Results._
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class ValidationControllerSpec extends CJWWSpec {

  val mockValidationService = mock[ValidationService]

  val testEncUserName = DataSecurity.encryptData[String]("testUserName").get
  val testEncEmail = DataSecurity.encryptData[String]("test@email.com").get

  class Setup {
    object TestController extends ValidationCtrl {
      val validationService = mockValidationService
    }
  }

  "ValidationController" should {
    "use the correct service" in {
      val controller = new ValidationController
      controller.validationService mustBe ValidationService
    }
  }

  "validateUserName" should {
    "return an OK" in new Setup {
      val request = FakeRequest().withHeaders("appID" -> AUTH_ID)

      when(mockValidationService.isUserNameInUse(Matchers.eq("testUserName")))
        .thenReturn(Future.successful(Ok))

      val result = TestController.validateUserName(testEncUserName)(request)
      status(result) mustBe OK
    }

    "return a CONFLICT" in new Setup {
      val request = FakeRequest().withHeaders("appID" -> AUTH_ID)

      when(mockValidationService.isUserNameInUse(Matchers.eq("testUserName")))
        .thenReturn(Future.successful(Conflict))

      val result = TestController.validateUserName(testEncUserName)(request)
      status(result) mustBe CONFLICT
    }

    "return an BAD REQUEST" in new Setup {
      val request = FakeRequest().withHeaders("appID" -> AUTH_ID)

      val result = TestController.validateUserName("INVALID_STRING")(request)
      status(result) mustBe BAD_REQUEST
    }

    "return a FORBIDDEN" in new Setup {
      val request = FakeRequest()

      val result = TestController.validateUserName(testEncUserName)(request)
      status(result) mustBe FORBIDDEN
    }
  }

  "validateEmail" should {
    "return an OK" in new Setup {
      val request = FakeRequest().withHeaders("appID" -> AUTH_ID)

      when(mockValidationService.isEmailInUse(Matchers.eq("test@email.com")))
        .thenReturn(Future.successful(Ok))

      val result = TestController.validateEmail(testEncEmail)(request)
      status(result) mustBe OK
    }

    "return a CONFLICT" in new Setup {
      val request = FakeRequest().withHeaders("appID" -> AUTH_ID)

      when(mockValidationService.isEmailInUse(Matchers.eq("test@email.com")))
        .thenReturn(Future.successful(Conflict))

      val result = TestController.validateEmail(testEncEmail)(request)
      status(result) mustBe CONFLICT
    }

    "return an BAD REQUEST" in new Setup {
      val request = FakeRequest().withHeaders("appID" -> AUTH_ID)

      val result = TestController.validateEmail("INVALID_STRING")(request)
      status(result) mustBe BAD_REQUEST
    }

    "return a FORBIDDEN" in new Setup {
      val request = FakeRequest()

      val result = TestController.validateEmail(testEncUserName)(request)
      status(result) mustBe FORBIDDEN
    }
  }
}
