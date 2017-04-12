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

import com.cjwwdev.mongo.{MongoFailedCreate, MongoFailedRead, MongoSuccessCreate, MongoSuccessRead}
import helpers.CJWWSpec
import models._
import repositories.LoginRepository
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers

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

    val testDefaultService = new LoginService(mockRepo)

    val testService = new LoginService(mockRepo) {
      override private[services] def processAuthContext(acc: UserAccount) = Future.successful(Some(testContext))
    }

    val testServiceFail = new LoginService(mockRepo) {
      override private[services] def processAuthContext(acc: UserAccount) = Future.successful(None)
    }
  }

  before(
    reset(mockRepo)
  )

  "login" should {
    "return an AuthContext" when {
      "the user has been successfully validated" in new Setup {
        when(mockRepo.validateIndividualUser(ArgumentMatchers.eq(testCredentials)))
          .thenReturn(Future.successful(Some(testUser)))

        val result = await(testService.login(testCredentials))
        result mustBe Some(testContext)
      }
    }

    "return None" when {
      "no valid user has been found" in new Setup {
        when(mockRepo.validateIndividualUser(ArgumentMatchers.eq(testCredentials)))
          .thenReturn(Future.successful(None))

        val result = await(testServiceFail.login(testCredentials))
        result mustBe None
      }

      "there was a problem caching the AuthContextDetail" in new Setup {
        when(mockRepo.validateIndividualUser(ArgumentMatchers.eq(testCredentials)))
          .thenReturn(Future.successful(Some(testUser)))

        val result = await(testServiceFail.login(testCredentials))
        result mustBe None
      }
    }
  }

  "getContext" should {
    "return an auth context" when {
      "a matching context is found" in new Setup {
        when(mockRepo.fetchContext(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoSuccessRead(testContext)))

        val result = await(testService.getContext("testUserId"))
        result mustBe Some(testContext)
      }
    }

    "return none" when {
      "no matching context is found" in new Setup {
        when(mockRepo.fetchContext(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoFailedRead))

        val result = await(testService.getContext("testUserId"))
        result mustBe None
      }
    }
  }

  "processAuthContext" should {
    "return an auth context" when {
      "one has been generated and cached" in new Setup {
        when(mockRepo.cacheContext(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoSuccessCreate))

        val result = await(testDefaultService.processAuthContext(testUser))
        result.get.user.userId mustBe "testUserId"
      }
    }

    "return none" when {
      "one has been generated but there was a problem caching" in new Setup {
        when(mockRepo.cacheContext(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoFailedCreate))

        val result = await(testDefaultService.processAuthContext(testUser))
        result mustBe None
      }
    }
  }
}
