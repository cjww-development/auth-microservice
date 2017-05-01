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

import com.cjwwdev.reactivemongo.{MongoFailedCreate, MongoSuccessCreate}
import helpers.CJWWSpec
import models._
import repositories.{ContextRepo, ContextRepository, LoginRepo, LoginRepository}
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers

import scala.concurrent.Future

class LoginServiceSpec extends CJWWSpec {

  val mockLoginRepo = mock[LoginRepository]
  val mockLoginStore = mock[LoginRepo]

  val mockContextRepo = mock[ContextRepository]
  val mockContextStore = mock[ContextRepo]

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

    val testDefaultService = new LoginService(mockLoginRepo, mockContextRepo) {
      override val loginStore: LoginRepo = mockLoginStore
      override val contextStore: ContextRepo = mockContextStore
    }

    val testService = new LoginService(mockLoginRepo, mockContextRepo) {
      override val loginStore: LoginRepo = mockLoginStore
      override val contextStore: ContextRepo = mockContextStore
      override private[services] def processAuthContext(acc: UserAccount) = Future.successful(Some(testContext))
    }

    val testServiceFail = new LoginService(mockLoginRepo, mockContextRepo) {
      override val loginStore: LoginRepo = mockLoginStore
      override val contextStore: ContextRepo = mockContextStore
      override private[services] def processAuthContext(acc: UserAccount) = Future.successful(None)
    }
  }

  before(
    reset(mockLoginRepo),
    reset(mockLoginStore),
    reset(mockContextRepo),
    reset(mockContextStore)
  )

  "login" should {
    "return an AuthContext" when {
      "the user has been successfully validated" in new Setup {
        when(mockLoginStore.validateIndividualUser(ArgumentMatchers.eq(testCredentials)))
          .thenReturn(Future.successful(testUser))

        val result = await(testService.login(testCredentials))
        result mustBe Some(testContext)
      }
    }

    "return None" when {
      "there was a problem caching the AuthContextDetail" in new Setup {
        when(mockLoginStore.validateIndividualUser(ArgumentMatchers.eq(testCredentials)))
          .thenReturn(Future.successful(testUser))

        val result = await(testServiceFail.login(testCredentials))
        result mustBe None
      }
    }
  }

  "getContext" should {
    "return an auth context" when {
      "a matching context is found" in new Setup {
        when(mockContextStore.fetchContext(ArgumentMatchers.any()))
          .thenReturn(Future.successful(testContext))

        val result = await(testService.getContext("testUserId"))
        result mustBe Some(testContext)
      }
    }
  }

  "processAuthContext" should {
    "return an auth context" when {
      "one has been generated and cached" in new Setup {
        when(mockContextStore.cacheContext(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoSuccessCreate))

        val result = await(testDefaultService.processAuthContext(testUser))
        result.get.user.userId mustBe "testUserId"
      }
    }

    "return none" when {
      "one has been generated but there was a problem caching" in new Setup {
        when(mockContextStore.cacheContext(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoFailedCreate))

        val result = await(testDefaultService.processAuthContext(testUser))
        result mustBe None
      }
    }
  }
}
