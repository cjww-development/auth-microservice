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

import com.cjwwdev.auth.models.{AuthContext, User}
import com.cjwwdev.reactivemongo.{MongoFailedCreate, MongoSuccessCreate}
import com.google.inject.Inject
import common.AuthContextNotFoundException
import models.{Login, OrgAccount, UserAccount}
import org.joda.time.DateTime
import repositories._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LoginServiceImpl @Inject()(val loginRepository: LoginRepository,
                                 val orgLoginRepository: OrgLoginRepository,
                                 val contextRepository: ContextRepository) extends LoginService

trait LoginService extends IdService {
  val loginRepository: LoginRepository
  val orgLoginRepository: OrgLoginRepository
  val contextRepository: ContextRepository

  private val INDIVIDUAL   = "individual"
  private val ORGANISATION = "organisation"

  def login(credentials : Login) : Future[Option[AuthContext]] = {
    loginRepository.validateIndividualUser(credentials) flatMap {
      case Some(acc)  => processUserAuthContext(acc)
      case None       => orgLoginRepository.validateOrganisationUser(credentials) flatMap {
        case Some(acc)  => processOrgAuthContext(acc)
        case None       => Future.successful(None)
      }
    }
  }

  def getContext(id : String) : Future[Option[AuthContext]] = {
    contextRepository.fetchContext(id) map {
      context => Some(context)
    } recover {
      case _: AuthContextNotFoundException => None
    }
  }

  private[services] def processUserAuthContext(acc : UserAccount) : Future[Option[AuthContext]] = {
    val generatedAuthContext = generateUserAuthContext(acc)
    contextRepository.cacheContext(generatedAuthContext) map {
      case MongoSuccessCreate   => Some(generatedAuthContext)
      case MongoFailedCreate    => None
    }
  }

  private[services] def processOrgAuthContext(acc: OrgAccount): Future[Option[AuthContext]] = {
    val generatedAuthContext = generateOrgAuthContext(acc)
    contextRepository.cacheContext(generatedAuthContext) map {
      case MongoSuccessCreate   => Some(generatedAuthContext)
      case MongoFailedCreate    => None
    }
  }

  def generateUserAuthContext(acc: UserAccount): AuthContext = {
    val role = acc.deversityDetails match {
      case Some(details)  => Some(details.role)
      case _              => None
    }

    AuthContext(
      contextId        = generateContextId,
      user             = User(acc.userId, Some(acc.firstName), Some(acc.lastName), None, INDIVIDUAL, role),
      basicDetailsUri  = s"/account/${acc.userId}/basic-details",
      enrolmentsUri    = s"/account/${acc.userId}/enrolments",
      settingsUri      = s"/account/${acc.userId}/settings",
      createdAt        = DateTime.now
    )
  }

  def generateOrgAuthContext(acc: OrgAccount): AuthContext = AuthContext(
    contextId        = generateContextId,
    user             = User(acc.orgId, None, None, Some(acc.orgName), ORGANISATION, None),
    basicDetailsUri  = s"/account/${acc.orgId}/basic-details",
    enrolmentsUri    = s"/account/${acc.orgId}/enrolments",
    settingsUri      = s"/account/${acc.orgId}/settings",
    createdAt        = DateTime.now
  )
}
