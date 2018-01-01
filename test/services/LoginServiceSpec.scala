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

import com.cjwwdev.auth.models.{AuthContext, User}
import com.cjwwdev.reactivemongo.{MongoFailedCreate, MongoSuccessCreate}
import helpers.CJWWSpec
import models._
import org.joda.time.DateTime
import repositories._
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers

import scala.concurrent.Future

class LoginServiceSpec extends CJWWSpec {

  val testCredentials = Login("testUser","testPass")

  val testUser = UserAccount("testUserId","testFirstName","testLastName","testUserName","test@email.com","testPass",None,DateTime.now,None)

  private val testContext = AuthContext(
    contextId = "context-test-context-id",
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
    createdAt = DateTime.now
  )

  class Setup {

    val testDefaultService = new LoginService {
      override val loginRepository    = mockLoginRepo
      override val orgLoginRepository = mockOrgLoginRepo
      override val contextRepository  = mockContextRepo
    }

    val testService = new LoginService {
      override val loginRepository    = mockLoginRepo
      override val orgLoginRepository = mockOrgLoginRepo
      override val contextRepository  = mockContextRepo

      override private[services] def processUserAuthContext(acc: UserAccount) = Future.successful(Some(testContext))
    }

    val testServiceFail = new LoginService {
      override val loginRepository    = mockLoginRepo
      override val orgLoginRepository = mockOrgLoginRepo
      override val contextRepository  = mockContextRepo

      override private[services] def processUserAuthContext(acc: UserAccount) = Future.successful(None)
    }
  }

  before(
    reset(mockLoginRepo),
    reset(mockContextRepo)
  )

  "login" should {
    "return an AuthContext" when {
      "the user has been successfully validated" in new Setup {
        when(mockLoginRepo.validateIndividualUser(ArgumentMatchers.eq(testCredentials)))
          .thenReturn(Future.successful(Some(testUser)))

        val result = await(testService.login(testCredentials))
        result mustBe Some(testContext)
      }
    }

    "return None" when {
      "there was a problem caching the AuthContextDetail" in new Setup {
        when(mockLoginRepo.validateIndividualUser(ArgumentMatchers.eq(testCredentials)))
          .thenReturn(Future.successful(Some(testUser)))

        val result = await(testServiceFail.login(testCredentials))
        result mustBe None
      }
    }
  }

  "getContext" should {
    "return an auth context" when {
      "a matching context is found" in new Setup {
        when(mockContextRepo.fetchContext(ArgumentMatchers.any()))
          .thenReturn(Future.successful(testContext))

        val result = await(testService.getContext("testUserId"))
        result mustBe Some(testContext)
      }
    }
  }

  "processAuthContext" should {
    "return an auth context" when {
      "one has been generated and cached" in new Setup {
        when(mockContextRepo.cacheContext(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoSuccessCreate))

        val result = await(testDefaultService.processUserAuthContext(testUser))
        result.get.user.id mustBe "testUserId"
      }
    }

    "return none" when {
      "one has been generated but there was a problem caching" in new Setup {
        when(mockContextRepo.cacheContext(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoFailedCreate))

        val result = await(testDefaultService.processUserAuthContext(testUser))
        result mustBe None
      }
    }
  }
}
