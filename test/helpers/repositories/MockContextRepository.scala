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

import com.cjwwdev.auth.models.CurrentUser
import com.cjwwdev.mongo.responses._
import common.CurrentUserNotFoundException
import helpers.other.Fixtures
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{reset, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import repositories.ContextRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait MockContextRepository extends BeforeAndAfterEach with MockitoSugar with Fixtures {
  self: PlaySpec =>

  val mockContextRepository = mock[ContextRepository]

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockContextRepository)
  }

  def mockCacheCurrentUser(cached: Boolean): OngoingStubbing[Future[MongoCreateResponse]] = {
    when(mockContextRepository.cacheCurrentUser(ArgumentMatchers.any()))
      .thenReturn(Future(if(cached) MongoSuccessCreate else MongoFailedCreate))
  }

  def mockFetchCurrentUser(fetched: Boolean): OngoingStubbing[Future[CurrentUser]] = {
    when(mockContextRepository.fetchCurrentUser(ArgumentMatchers.any()))
      .thenReturn(if(fetched) Future(testCurrentUser) else Future.failed(new CurrentUserNotFoundException("")))
  }
}
