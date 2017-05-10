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
package app

import com.cjwwdev.security.encryption.DataSecurity
import models.Login
import play.api.test.Helpers._
import utils.CJWWIntegrationUtils

class AuthMicroserviceISpec extends CJWWIntegrationUtils {

  "/auth/login/user (individual)" should {
    "return an OK" when {
      "the user has been successfully validated" in {
        beforeITest()

        val enc = DataSecurity.encryptData[Login](Login("testUserName", "testPassword")).get
        val request = client(s"$baseUrl/login/user?enc=$enc")
          .withHeaders(
            "appId" -> "abda73f4-9d52-4bb8-b20d-b5fffd0cc130",
            CONTENT_TYPE -> TEXT
          ).get()

        val result = await(request)
        result.status mustBe OK

        afterITest()
      }
    }

    "return a FORBIDDEN" when {
      "the user has not been successfully validated" in {
        val enc = DataSecurity.encryptData[Login](Login("testUserName", "testPassword")).get
        val request = client(s"$baseUrl/login/user?enc=$enc")
          .withHeaders(
            "appId" -> "abda73f4-9d52-4bb8-b20d-b5fffd0cc130",
            CONTENT_TYPE -> TEXT
          ).get()

        val result = await(request)
        result.status mustBe FORBIDDEN
      }

      "the request is not authorised" in {
        val enc = DataSecurity.encryptData[Login](Login("testUserName", "testPassword")).get
        val request = client(s"$baseUrl/login/user?enc=$enc")
          .withHeaders(
            CONTENT_TYPE -> TEXT
          ).get()

        val result = await(request)
        result.status mustBe FORBIDDEN
      }
    }
  }

  "/auth/login/user (organisation)" should {
    "return an OK" when {
      "the user has been successfully validated" in {
        beforeITest()

        val enc = DataSecurity.encryptData[Login](Login("testOrgUserName", "testPass")).get
        val request = client(s"$baseUrl/login/user?enc=$enc")
          .withHeaders(
            "appId" -> "abda73f4-9d52-4bb8-b20d-b5fffd0cc130",
            CONTENT_TYPE -> TEXT
          ).get()

        val result = await(request)
        result.status mustBe OK

        afterITest()
      }
    }

    "return a FORBIDDEN" when {
      "the user has not been successfully validated" in {
        val enc = DataSecurity.encryptData[Login](Login("testOrgUserName", "testPass")).get
        val request = client(s"$baseUrl/login/user?enc=$enc")
          .withHeaders(
            "appId" -> "abda73f4-9d52-4bb8-b20d-b5fffd0cc130",
            CONTENT_TYPE -> TEXT
          ).get()

        val result = await(request)
        result.status mustBe FORBIDDEN
      }

      "the request is not authorised" in {
        val enc = DataSecurity.encryptData[Login](Login("testOrgUserName", "testPass")).get
        val request = client(s"$baseUrl/login/user?enc=$enc")
          .withHeaders(
            CONTENT_TYPE -> TEXT
          ).get()

        val result = await(request)
        result.status mustBe FORBIDDEN
      }
    }
  }

  "/auth/get-context/{contextId}" should {
    "return an OK" when {
      "a context has been found" in {
        beforeITest()

        val request = client(s"$baseUrl/get-context/context-test-context-id")
          .withHeaders(
            "appId" -> "abda73f4-9d52-4bb8-b20d-b5fffd0cc130",
            CONTENT_TYPE -> TEXT
          ).get()

        val result = await(request)
        result.status mustBe OK

        afterITest()
      }
    }

    "return a NOT FOUND" when {
      "a context has not been found" in {
        val request = client(s"$baseUrl/get-context/context-test-context-id")
          .withHeaders(
            "appId" -> "abda73f4-9d52-4bb8-b20d-b5fffd0cc130",
            CONTENT_TYPE -> TEXT
          ).get()

        val result = await(request)
        result.status mustBe NOT_FOUND
      }
    }

    "return a FORBIDDEN" when {
      "the request is not authorised" in {
        val request = client(s"$baseUrl/get-context/context-test-context-id")
          .withHeaders(
            CONTENT_TYPE -> TEXT
          ).get()

        val result = await(request)
        result.status mustBe FORBIDDEN
      }
    }
  }
}
