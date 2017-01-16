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
package utils.security

import helpers.CJWWSpec
import models.UserAccount

class DataSecuritySpec extends CJWWSpec {

  val user =
    UserAccount(
      None,
      "testFirstName",
      "testLastName",
      "testUserName",
      "test@email.com",
      "testPass",
      None,
      None,
      None
    )

  class Setup {
    object TestSec extends DataSecurity
  }

  "encryptData" should {
    "scramble the data, i.e not match what was put in" in new Setup {
      val result = DataSecurity.encryptData[UserAccount](user)
      result.get.getClass mustBe classOf[String]
    }
  }
}
