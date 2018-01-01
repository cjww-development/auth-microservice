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
package utils

import java.util.UUID

import com.cjwwdev.auth.models.{AuthContext, User}
import models.{OrgAccount, UserAccount}
import org.joda.time.DateTime
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.ws.{WS, WSClient, WSRequest}
import repositories._

import scala.concurrent.{Await, Awaitable}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

trait CJWWIntegrationUtils extends PlaySpec with GuiceOneServerPerSuite {

  val loginRepo    = new LoginRepositoryImpl
  val contextRepo  = new ContextRepositoryImpl
  val orgLoginRepo = new OrgLoginRepositoryImpl

  val baseUrl = s"http://localhost:$port/auth"

  val uuid: UUID = UUID.randomUUID

  lazy val ws = app.injector.instanceOf(classOf[WSClient])

  def client(url: String): WSRequest = ws.url(url)

  def await[T](awaitable: Awaitable[T]): T = Await.result(awaitable, 5.seconds)

  private val testAccount = UserAccount(
    userId = s"user-$uuid",
    firstName = "testFirstName",
    lastName = "testLastName",
    userName = "testUserName",
    email = "test@email.com",
    password = "testPassword",
    deversityDetails = None,
    createdAt  = DateTime.now,
    enrolments = None,
    settings = None
  )

  private val testContext = AuthContext(
    contextId = s"context-$uuid",
    user = User(
      id = "user-test-user-id",
      firstName = Some("testFirstName"),
      lastName = Some("testLastName"),
      orgName = None,
      "individual",
      None
    ),
    basicDetailsUri = "/test/uri",
    enrolmentsUri = "/test/uri",
    settingsUri = "/test/uri",
    createdAt = DateTime.now
  )

  private val testOrgAccount = OrgAccount(
    orgId = s"org-$uuid",
    orgName = "testOrgName",
    initials = "TI",
    orgUserName = "testOrgUserName",
    location = "testLocation",
    orgEmail = "test@email.com",
    credentialType = "organisation",
    password = "testPass",
    createdAt = DateTime.now,
    settings = None
  )

  def beforeITest(): Unit = {
    await(loginRepo.collection.flatMap(_.insert(testAccount)))
    await(contextRepo.collection.flatMap(_.insert(testContext)))
    await(orgLoginRepo.collection.flatMap(_.insert(testOrgAccount)))
  }

  def afterITest(): Unit = {
    await(loginRepo.collection.flatMap(_.drop(failIfNotFound = false)))
    await(contextRepo.collection.flatMap(_.drop(failIfNotFound = false)))
    await(orgLoginRepo.collection.flatMap(_.drop(failIfNotFound = false)))
  }
}
