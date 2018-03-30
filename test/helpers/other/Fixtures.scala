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

package helpers.other

import com.cjwwdev.auth.models.CurrentUser
import models._
import play.api.libs.json.Json

trait Fixtures extends TestDataGenerator {

  val testOrgDevId = generateTestSystemId(DEVERSITY)
  val testUserId   = generateTestSystemId(USER)

  val testCurrentUser = CurrentUser(
    contextId       = generateTestSystemId(CONTEXT),
    id              = generateTestSystemId(USER),
    orgDeversityId  = None,
    credentialType  = "individual",
    orgName         = None,
    firstName       = Some("testFirstName"),
    lastName        = Some("testLastName"),
    role            = Some("teacher"),
    enrolments      = Some(Json.obj(
      "deversityId" -> generateTestSystemId(DEVERSITY)
    ))
  )

  def testTeacherEnrolment: DeversityEnrolment = {
    DeversityEnrolment(
      statusConfirmed = "pending",
      schoolName      = testOrgDevId,
      role            = "teacher",
      title           = Some("testTitle"),
      room            = Some("testRoom"),
      teacher         = None
    )
  }

  def testStudentEnrolment: DeversityEnrolment = {
    DeversityEnrolment(
      statusConfirmed = "pending",
      schoolName      = testOrgDevId,
      role            = "student",
      title           = None,
      room            = None,
      teacher         = Some(createTestUserName)
    )
  }

  def testUserAccount(accountType: AccountEnums.Value): UserAccount = {
    val accType = if(accountType == AccountEnums.teacher) {
      Some(testTeacherEnrolment)
    } else if(accountType == AccountEnums.student) {
      Some(testStudentEnrolment)
    } else {
      None
    }

    val enrs = if(accountType == AccountEnums.teacher | accountType == AccountEnums.student) {
      Some(Enrolments(None, None, Some(generateTestSystemId(DEVERSITY))))
    } else {
      None
    }

    UserAccount(
      userId            = generateTestSystemId(USER),
      firstName         = "testFirstName",
      lastName          = "testLastName",
      userName          = "testUserName",
      email             = "test@email.com",
      password          = "testPass",
      deversityDetails  = accType,
      createdAt         = now,
      enrolments        = enrs,
      settings          = None
    )
  }

  val testOrgAccount = OrgAccount(
    orgId           = generateTestSystemId(ORG),
    deversityId     = testOrgDevId,
    orgName         = "testSchoolName",
    initials        = "TSN",
    orgUserName     = "tSchoolName",
    location        = "testLocation",
    orgEmail        = "test@email.com",
    credentialType  = "organisation",
    password        = "testPass",
    createdAt       = now,
    settings        = None
  )

  val testCredentials = Login(
    username = "testUserName",
    password = "testPassword"
  )
}
