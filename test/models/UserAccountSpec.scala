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
package models

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class UserAccountSpec extends PlaySpec {

  val testJson =
    """
      |{
      |    "firstName" : "testFirstName",
      |    "lastName" : "testLastName",
      |    "userName" : "testUserName",
      |    "email" : "test@email.com",
      |    "password" : "testPass",
      |    "enrolments" : {
      |        "hubId" : "testId",
      |        "diagId" : "testId",
      |        "deversityId" : "testId"
      |    },
      |    "settings" : {
      |        "displayName" : "testString",
      |        "displayNameColour" : "#123456",
      |        "displayImageUrl" : "testurl.com"
      |    }
      |}
    """.stripMargin

  val testUser =
    UserAccount(
      None,
      "testFirstName",
      "testLastName",
      "testUserName",
      "test@email.com",
      "testPass",
      None,
      None,
      Some(Enrolments(
        Some("testId"),
        Some("testId"),
        Some("testId")
      )),
      Some(Map(
        "displayName" -> "testString",
        "displayNameColour" -> "#123456",
        "displayImageUrl" -> "testurl.com"
      ))
    )

  "A user account" should {
    "be parsed into JSON" in {
      val result = Json.toJson[UserAccount](testUser)
      result mustBe Json.parse(testJson)
    }
  }
}
