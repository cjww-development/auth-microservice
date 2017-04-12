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
package repositories

import com.cjwwdev.mongo._
import helpers.CJWWSpec
import models._
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers
import play.api.libs.json.OFormat
import com.cjwwdev.security.encryption.DataSecurity

import scala.concurrent.Future

class LoginRepositorySpec extends CJWWSpec {

  val mockConnector = mock[MongoConnector]

  val testLogin = Login("testUser","testPass")

  val testUserAccount = UserAccount(Some("testUserId"),"testFirstName","testLastName","testUserName","test@email.com","testPass",None,None,None)

  val scrambledId = DataSecurity.encryptData[String](testUserAccount._id.get)

  val testContext =
    AuthContext(
      _id     =  "testContextId",
      user = User("user-1234567890","testFirstName","testLastName"),
      basicDetailsUri  = s"/accounts/basic-details/$scrambledId",
      enrolmentsUri = s"/accounts/enrolments/$scrambledId",
      settingsUri   = s"/accounts/settings/$scrambledId"
    )

  class Setup {
    val testRepo = new LoginRepository(mockConnector)
  }

  "validateIndividualUser" should {
    "return None" when {
      "the users credential don't match anything on record" in new Setup {
        when(mockConnector.read[UserAccount](ArgumentMatchers.eq(USER_ACCOUNTS), ArgumentMatchers.any())(ArgumentMatchers.any[OFormat[UserAccount]]()))
          .thenReturn(Future.successful(MongoFailedRead))

        val result = await(testRepo.validateIndividualUser(testLogin))
        result mustBe None
      }
    }

    "return an AuthContext" when {
      "the user has been successfully validated" in new Setup {
        when(mockConnector.read[UserAccount](ArgumentMatchers.eq(USER_ACCOUNTS), ArgumentMatchers.any())(ArgumentMatchers.any[OFormat[UserAccount]]()))
          .thenReturn(Future.successful(MongoSuccessRead(testUserAccount)))

        val result = await(testRepo.validateIndividualUser(testLogin))
        result mustBe Some(testUserAccount)
      }
    }
  }

  "cacheContext" should {
    "return a MongoCreateResponse" when {
      "an auth context has been cached" in new Setup {
        when(mockConnector.create(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoSuccessCreate))

        val result = await(testRepo.cacheContext(testContext))
        result mustBe MongoSuccessCreate
      }

      "there was a problem caching the context" in new Setup {
        when(mockConnector.create(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoFailedCreate))

        val result = await(testRepo.cacheContext(testContext))
        result mustBe MongoFailedCreate
      }
    }
  }

  "fetchContext" should {
    "return a MongoReadResponse" when {
      "a matching auth context has been found" in new Setup {
        when(mockConnector.read[AuthContext](ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoSuccessRead(testContext)))

        val result = await(testRepo.fetchContext("user-1234567890"))
        result mustBe MongoSuccessRead(testContext)
      }

      "no matching auth context has been found" in new Setup {
        when(mockConnector.read[AuthContext](ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoFailedRead))

        val result = await(testRepo.fetchContext("user-1234567890"))
        result mustBe MongoFailedRead
      }
    }
  }
}
