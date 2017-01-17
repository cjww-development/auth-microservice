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

import helpers.CJWWSpec
import repositories._
import org.mockito.Mockito._
import org.mockito.Matchers
import play.api.test.Helpers._

import scala.concurrent.Future

class ValidationServiceSpec extends CJWWSpec {

  val mockRepo = mock[RegistrationRepository]

  class Setup {
    object TestService extends ValidationService {
      val regRepo = mockRepo
    }
  }

  "isUserNameInUse" should {
    "return a continue" when {
      "the given user name is not in use" in new Setup {
        when(mockRepo.verifyUserName(Matchers.eq("testUserName")))
          .thenReturn(Future.successful(UserNameNotInUse))

        val result = TestService.isUserNameInUse("testUserName")
        status(result) mustBe OK
      }
    }

    "return a conflict" when {
      "the given user name is already in use" in new Setup {
        when(mockRepo.verifyUserName(Matchers.eq("testUserName")))
          .thenReturn(Future.successful(UserNameInUse))

        val result = TestService.isUserNameInUse("testUserName")
        status(result) mustBe CONFLICT
      }
    }

    "return an internal server error" when {
      "something unexpected is returned from verifyUserName" in new Setup {
        when(mockRepo.verifyUserName(Matchers.eq("testUserName")))
          .thenReturn(Future.successful(EmailInUse))

        val result = TestService.isUserNameInUse("testUserName")
        status(result) mustBe INTERNAL_SERVER_ERROR
      }
    }
  }

  "isEmailInUse" should {
    "return a continue" when {
      "the given email is not in use" in new Setup {
        when(mockRepo.verifyEmail(Matchers.eq("test@email.com")))
          .thenReturn(Future.successful(EmailNotInUse))

        val result = TestService.isEmailInUse("test@email.com")
        status(result) mustBe OK
      }
    }

    "return a conflict" when {
      "the given user name is already in use" in new Setup {
        when(mockRepo.verifyEmail(Matchers.eq("test@email.com")))
          .thenReturn(Future.successful(EmailInUse))

        val result = TestService.isEmailInUse("test@email.com")
        status(result) mustBe CONFLICT
      }
    }

    "return an internal server error" when {
      "something unexpected is returned from verifyEmail" in new Setup {
        when(mockRepo.verifyEmail(Matchers.eq("test@email.com")))
          .thenReturn(Future.successful(UserNameInUse))

        val result = TestService.isEmailInUse("test@email.com")
        status(result) mustBe INTERNAL_SERVER_ERROR
      }
    }
  }
}
