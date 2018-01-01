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

import org.scalatestplus.play.PlaySpec

class IdServiceSpec extends PlaySpec {

  class Setup {
    object TestService extends IdService
  }

  "generateHubId" should {
    "return a string that contains 'hub' and is exactly 40 characters long" in new Setup {
      val resultOne   = TestService.generateHubId
      val resultTwo   = TestService.generateHubId
      val resultThree = TestService.generateHubId
      val resultFour  = TestService.generateHubId
      val resultFive  = TestService.generateHubId

      assert(resultOne.contains("hub"))
      assert(resultTwo.contains("hub"))
      assert(resultThree.contains("hub"))
      assert(resultFour.contains("hub"))
      assert(resultFive.contains("hub"))

      resultOne.length mustBe 40
      resultTwo.length mustBe 40
      resultThree.length mustBe 40
      resultFour.length mustBe 40
      resultFive.length mustBe 40
    }
  }

  "generateDiagnosticsId" should {
    "return a string that contains 'diag' and is exactly 41 characters long" in new Setup {
      val resultOne   = TestService.generateDiagnosticsId
      val resultTwo   = TestService.generateDiagnosticsId
      val resultThree = TestService.generateDiagnosticsId
      val resultFour  = TestService.generateDiagnosticsId
      val resultFive  = TestService.generateDiagnosticsId

      assert(resultOne.contains("diag"))
      assert(resultTwo.contains("diag"))
      assert(resultThree.contains("diag"))
      assert(resultFour.contains("diag"))
      assert(resultFive.contains("diag"))

      resultOne.length mustBe 41
      resultTwo.length mustBe 41
      resultThree.length mustBe 41
      resultFour.length mustBe 41
      resultFive.length mustBe 41
    }
  }

  "generateDeversityId" should {
    "return a string that contains 'deversity' and is exactly 46 characters long" in new Setup {
      val resultOne   = TestService.generateDeversityId
      val resultTwo   = TestService.generateDeversityId
      val resultThree = TestService.generateDeversityId
      val resultFour  = TestService.generateDeversityId
      val resultFive  = TestService.generateDeversityId

      assert(resultOne.contains("deversity"))
      assert(resultTwo.contains("deversity"))
      assert(resultThree.contains("deversity"))
      assert(resultFour.contains("deversity"))
      assert(resultFive.contains("deversity"))

      resultOne.length mustBe 46
      resultTwo.length mustBe 46
      resultThree.length mustBe 46
      resultFour.length mustBe 46
      resultFive.length mustBe 46
    }
  }
}
