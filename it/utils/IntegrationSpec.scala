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

import akka.util.Timeout
import com.cjwwdev.http.headers.HeaderPackage
import com.cjwwdev.testing.integration.IntegrationTestSpec
import com.cjwwdev.testing.integration.application.IntegrationApplication
import com.cjwwdev.testing.integration.wiremock.WireMockSetup
import play.api.libs.ws.WSRequest
import repositories.{ContextRepository, LoginRepository, OrgLoginRepository}

import scala.concurrent.duration._

trait IntegrationSpec
  extends IntegrationTestSpec
    with Fixtures
    with IntegrationApplication
    with WireMockSetup {

  override implicit def defaultAwaitTimeout: Timeout = 5.seconds

  val testContextId   = s"""{"contextId" : "${generateTestSystemId(CONTEXT)}"}"""
  val testOrgId       = generateTestSystemId(ORG)
  val testUserId      = generateTestSystemId(USER)
  val testDeversityId = generateTestSystemId(DEVERSITY)

  override val appConfig = Map(
    "repositories.ContextRepositoryImpl.collection"  -> "it-contexts",
    "repositories.OrgLoginRepositoryImpl.collection" -> "it-org-accounts",
    "repositories.LoginRepositoryImpl.collection"    -> "it-user-accounts"
  )

  override val currentAppBaseUrl = "auth"

  lazy val contextRepository  = app.injector.instanceOf[ContextRepository]
  lazy val loginRepository    = app.injector.instanceOf[LoginRepository]
  lazy val orgLoginRepository = app.injector.instanceOf[OrgLoginRepository]

  val testCookieId = generateTestSystemId(SESSION)

  def client(url: String): WSRequest = ws.url(url).withHeaders(
    "cjww-headers" -> HeaderPackage("abda73f4-9d52-4bb8-b20d-b5fffd0cc130", testCookieId).encryptType,
    CONTENT_TYPE   -> TEXT
  )

  private def dropDbs(): Unit = {
    await(contextRepository.collection.flatMap(_.drop(failIfNotFound = false)))
    await(contextRepository.collection.flatMap(_.drop(failIfNotFound = false)))
    await(orgLoginRepository.collection.flatMap(_.drop(failIfNotFound = false)))
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    dropDbs()
    resetWm()
  }

  override def beforeAll(): Unit = {
    super.beforeAll()
    startWm()
  }

  override def afterEach(): Unit = {
    super.afterEach()
    dropDbs()
  }

  override def afterAll(): Unit = {
    super.afterAll()
    dropDbs()
    stopWm()
  }
}
