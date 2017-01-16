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

import config.{MongoFailedCreate, MongoSuccessCreate, MongoSuccessDelete}
import models.UserAccount
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import repositories.RegistrationRepository
import org.mockito.Mockito._
import org.mockito.Matchers
import play.api.test.Helpers._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class RegistrationServiceSpec extends PlaySpec with MockitoSugar {

  val mockRepo = mock[RegistrationRepository]

  val user = UserAccount(None, "testFirstName", "testLastName", "testUserName", "test@email.com", "testPass", None, None, None)

  class Setup {
    object TestService extends RegistrationService {
      val regRepo = mockRepo
    }
  }

  "createNewUser" should {
    "return a Ok" when {
      "the given user has been inserted into the database" in new Setup {
        when(mockRepo.insertNewUser(Matchers.any[UserAccount]()))
          .thenReturn(Future.successful(MongoSuccessCreate))

        val result = TestService.createNewUser(user)
        status(result) mustBe CREATED
      }
    }

    "return an internal server error" when {
      "there were problems inserting the given into the database" in new Setup {
        when(mockRepo.insertNewUser(Matchers.any[UserAccount]()))
          .thenReturn(Future.successful(MongoFailedCreate))

        val result = TestService.createNewUser(user)
        status(result) mustBe INTERNAL_SERVER_ERROR
      }
    }

    "throw an IllegalStateException" in new Setup {
      when(mockRepo.insertNewUser(Matchers.any[UserAccount]()))
        .thenReturn(Future.successful(MongoSuccessDelete))

      val result = intercept[IllegalStateException] {
        Await.result(TestService.createNewUser(user), 5.seconds)
      }

      result.getClass mustBe classOf[IllegalStateException]
    }
  }
}
