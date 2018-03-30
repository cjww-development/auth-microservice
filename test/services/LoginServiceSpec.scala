/*
 * Copyright 2018 CJWW Development
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package services

import helpers.other.AccountEnums
import helpers.services.ServiceSpec
import play.api.libs.json.Json

class LoginServiceSpec extends ServiceSpec {
  class Setup {
    val testService = new LoginService {
      override val loginRepository    = mockLoginRepository
      override val orgLoginRepository = mockOrgLoginRepository
      override val contextRepository  = mockContextRepository
    }
  }

  "login" should {
    "return an CurrentUser" when {
      "the user has been successfully validated" in new Setup {
        mockValidateIndividualUser(validated = true)

        mockCacheCurrentUser(cached = true)

        awaitAndAssert(testService.login(testCredentials)) {
          case Some(res) =>
            res.id              mustBe testCurrentUser.id
            res.credentialType  mustBe testCurrentUser.credentialType
            res.enrolments      mustBe testCurrentUser.enrolments
            res.firstName       mustBe testCurrentUser.firstName
            res.lastName        mustBe testCurrentUser.lastName
            res.orgDeversityId  mustBe testCurrentUser.orgDeversityId
            res.orgName         mustBe testCurrentUser.orgName
            res.role            mustBe testCurrentUser.role
        }
      }
    }

    "return None" when {
      "there was a problem caching the AuthContextDetail" in new Setup {
        mockValidateIndividualUser(validated = true)

        mockCacheCurrentUser(cached = false)

        awaitAndAssert(testService.login(testCredentials)) {
          _ mustBe None
        }
      }
    }
  }

  "getContext" should {
    "return an auth context" when {
      "a matching context is found" in new Setup {
        mockFetchCurrentUser(fetched = true)

        awaitAndAssert(testService.getContext(testUserId)) {
          _ mustBe Some(testCurrentUser)
        }
      }
    }
  }

  "processAuthContext" should {
    "return an auth context" when {
      "one has been generated and cached" in new Setup {
        mockCacheCurrentUser(cached = true)

        awaitAndAssert(testService.processUserAuthContext(testUserAccount(AccountEnums.basic))) { res =>
          assert(res.isDefined)
          res.get.id mustBe testUserId
        }
      }
    }

    "return none" when {
      "one has been generated but there was a problem caching" in new Setup {
        mockCacheCurrentUser(cached = false)

        awaitAndAssert(testService.processUserAuthContext(testUserAccount(AccountEnums.basic))) { res =>
          assert(res.isEmpty)
        }
      }
    }
  }
}
