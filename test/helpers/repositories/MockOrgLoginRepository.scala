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

package helpers.repositories

import helpers.other.Fixtures
import models.OrgAccount
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{reset, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import repositories.OrgLoginRepository

import scala.concurrent.Future

trait MockOrgLoginRepository extends BeforeAndAfterEach with MockitoSugar with Fixtures {
  self: PlaySpec =>

  val mockOrgLoginRepository = mock[OrgLoginRepository]

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockOrgLoginRepository)
  }

  def mockValidateOrganisationUser(validated: Boolean): OngoingStubbing[Future[Option[OrgAccount]]] = {
    when(mockOrgLoginRepository.validateOrganisationUser(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful(if(validated) Some(testOrgAccount) else None))
  }
}
