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
package app

import models.Login
import utils.{IntegrationSpec, IntegrationStubbing}

class AuthMicroserviceISpec extends IntegrationSpec with IntegrationStubbing {

  "/auth/login/user (individual)" should {
    val enc = Login("testUserName", "testPassword").encryptType
    "return an OK" when {
      "the user has been successfully validated" in {
        given
          .individualUser.isSetup

        awaitAndAssert(client(s"$testAppUrl/login/user?enc=$enc").get()) {
          _.status mustBe OK
        }
      }
    }

    "return a FORBIDDEN" when {
      "the user has not been successfully validated" in {
        val enc = Login("testUserName", "testInvalidPass").encryptType
        awaitAndAssert(client(s"$testAppUrl/login/user?enc=$enc").get()) {
          _.status mustBe FORBIDDEN
        }
      }

      "the request is not authorised" in {
        awaitAndAssert(ws.url(s"$testAppUrl/login/user?enc=$enc").get()) {
          _.status mustBe FORBIDDEN
        }
      }
    }
  }

  "/auth/login/user (organisation)" should {
    val enc = Login("testOrgUserName", "testPass").encryptType
    "return an OK" when {
      "the user has been successfully validated" in {
        given
          .organisationUser.isSetup

        awaitAndAssert(client(s"$testAppUrl/login/user?enc=$enc").get()) {
          _.status mustBe OK
        }
      }
    }

    "return a FORBIDDEN" when {
      "the user has not been successfully validated" in {
        awaitAndAssert(client(s"$testAppUrl/login/user?enc=$enc").get()) {
          _.status mustBe FORBIDDEN
        }
      }

      "the request is not authorised" in {
        awaitAndAssert(ws.url(s"$testAppUrl/login/user?enc=$enc").get()) {
          _.status mustBe FORBIDDEN
        }
      }
    }
  }

  "/auth/get-context/{contextId}" should {
    "return an OK" when {
      "a context has been found" in {
        given
          .currentUsers.hasCurrentUser

        awaitAndAssert(client(s"$testAppUrl/get-current-user/${generateTestSystemId(CONTEXT)}").get()) {
          _.status mustBe OK
        }
      }
    }

    "return a NOT FOUND" when {
      "a context has not been found" in {
        awaitAndAssert(client(s"$testAppUrl/get-current-user/${generateTestSystemId(CONTEXT)}").get()) {
          _.status mustBe NOT_FOUND
        }
      }
    }

    "return a FORBIDDEN" when {
      "the request is not authorised" in {
        awaitAndAssert(ws.url(s"$testAppUrl/get-current-user/${generateTestSystemId(CONTEXT)}").get()) {
          _.status mustBe FORBIDDEN
        }
      }
    }
  }
}
