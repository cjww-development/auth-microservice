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

package helpers.services

import com.cjwwdev.auth.models.CurrentUser
import helpers.other.Fixtures
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{reset, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import services.LoginService

import scala.concurrent.Future

trait MockLoginService extends BeforeAndAfterEach with MockitoSugar with Fixtures {
  self: PlaySpec =>

  val mockLoginService = mock[LoginService]

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockLoginService)
  }

  def mockLogin(loggedIn: Boolean): OngoingStubbing[Future[Option[CurrentUser]]] = {
    when(mockLoginService.login(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful(if(loggedIn) Some(testCurrentUser) else None))
  }

  def mockGetContext(fetched: Boolean): OngoingStubbing[Future[Option[CurrentUser]]] = {
    when(mockLoginService.getContext(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful(if(fetched) Some(testCurrentUser) else None))
  }
}
