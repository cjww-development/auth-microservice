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

package utils

import com.cjwwdev.auth.models.CurrentUser
import com.cjwwdev.implicits.ImplicitHandlers
import models.{OrgAccount, UserAccount}
import org.joda.time.DateTime
import play.api.libs.json.Json

trait Fixtures extends TestDataGenerator with ImplicitHandlers {

  val testAccount = UserAccount(
    userId            = generateTestSystemId(USER),
    firstName         = "testFirstName",
    lastName          = "testLastName",
    userName          = "testUserName",
    email             = "test@email.com",
    password          = "testPassword",
    deversityDetails  = None,
    createdAt         = DateTime.now,
    enrolments        = None,
    settings          = None
  )

  val testCurrentUser = CurrentUser(
    contextId       = generateTestSystemId(CONTEXT),
    id              = generateTestSystemId(USER),
    orgDeversityId  = Some(generateTestSystemId(DEVERSITY)),
    credentialType  = "individual",
    orgName         = None,
    firstName       = Some("testFirstName"),
    lastName        = Some("testLastName"),
    role            = None,
    enrolments      = Some(Json.obj(
      "deversityId" -> generateTestSystemId(DEVERSITY)
    ))
  )

  val testOrgAccount = OrgAccount(
    orgId           = generateTestSystemId(ORG),
    deversityId     = generateTestSystemId(DEVERSITY),
    orgName         = "testOrgName",
    initials        = "TI",
    orgUserName     = "testOrgUserName",
    location        = "testLocation",
    orgEmail        = "test@email.com",
    credentialType  = "organisation",
    password        = "testPass",
    createdAt       = DateTime.now,
    settings        = None
  )
}
