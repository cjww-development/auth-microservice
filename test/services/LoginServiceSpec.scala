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

import config.MongoSuccessCreate
import helpers.CJWWSpec
import models.{AuthContext, Login, User, UserAccount}
import repositories.LoginRepository
import org.mockito.Mockito._
import org.mockito.Matchers
import play.api.test.Helpers._

import scala.concurrent.Future

class LoginServiceSpec extends CJWWSpec {

  val mockRepo = mock[LoginRepository]

  val testCredentials = Login("testUser","testPass")

  val testUser = UserAccount(Some("testUserId"),"testFirstName","testLastName","testUserName","test@email.com","testPass",None,None,None)

  val testContext =
    AuthContext(
      "testContextId",
      User("testUserId","testFirstName","testLastName"),
      "testUri",
      "testUri",
      "testUri"
    )

  class Setup {
    val testService = new LoginService(mockRepo)
  }


  "login" should {
    "return forbidden" when {
      "a user with the input credentials isnt found" in new Setup {
        when(mockRepo.validateIndividualUser(Matchers.eq(testCredentials)))
          .thenReturn(Future.successful(None))

        val result = testService.login(testCredentials)
        status(result) mustBe FORBIDDEN
      }
    }

//    "return ok" when {
//      "the user has been successfully validated" in new Setup {
//        when(mockRepo.validateIndividualUser(Matchers.eq(testCredentials)))
//          .thenReturn(Future.successful(Some(testUser)))
//
//        when(mockRepo.cacheContext(Matchers.eq(testContext)))
//          .thenReturn(Future.successful(MongoSuccessCreate))
//
//        val result = testService.login(testCredentials)
//        status(result) mustBe OK
//      }
//    }
  }
}
