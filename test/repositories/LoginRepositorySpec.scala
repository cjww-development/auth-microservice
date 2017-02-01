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

import config.{MongoFailedRead, MongoSuccessRead}
import connectors.MongoConnector
import helpers.CJWWSpec
import models.{AuthContext, Login, User, UserAccount}
import org.mockito.Mockito._
import org.mockito.Matchers
import play.api.libs.json.OFormat
import reactivemongo.bson.BSONDocument
import utils.security.DataSecurity

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
        when(mockConnector.read[UserAccount](Matchers.eq(USER_ACCOUNTS), Matchers.any[BSONDocument]())(Matchers.any[OFormat[UserAccount]]()))
          .thenReturn(Future.successful(MongoFailedRead))

        val result = await(testRepo.validateIndividualUser(testLogin))
        result mustBe None
      }
    }

//    "return an AuthContext" when {
//      "the user has been successfully validated" in new Setup {
//        when(mockConnector.read[UserAccount](Matchers.eq(USER_ACCOUNTS), Matchers.any[BSONDocument]())(Matchers.any[OFormat[UserAccount]]()))
//          .thenReturn(Future.successful(MongoSuccessRead(testUserAccount)))
//
//        val result = await(testRepo.validateIndividualUser(testLogin))
//
//        result.get.basicDetailsUri mustBe testContext.basicDetailsUri
//        result.get.enrolmentsUri mustBe testContext.enrolmentsUri
//        result.get.settingsUri mustBe testContext.settingsUri
//      }
//    }
  }
}
