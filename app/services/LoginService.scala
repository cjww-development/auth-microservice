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
package services

import com.cjwwdev.auth.models.CurrentUser
import com.cjwwdev.mongo.responses.{MongoFailedCreate, MongoSuccessCreate}
import common.CurrentUserNotFoundException
import javax.inject.Inject
import models.{Login, OrgAccount, UserAccount}
import play.api.libs.json.{JsObject, Json}
import repositories._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DefaultLoginService @Inject()(val loginRepository: LoginRepository,
                                    val orgLoginRepository: OrgLoginRepository,
                                    val contextRepository: ContextRepository) extends LoginService

trait LoginService extends IdService {
  val loginRepository: LoginRepository
  val orgLoginRepository: OrgLoginRepository
  val contextRepository: ContextRepository

  private val INDIVIDUAL   = "individual"
  private val ORGANISATION = "organisation"

  def login(credentials : Login) : Future[Option[CurrentUser]] = {
    loginRepository.validateIndividualUser(credentials) flatMap {
      case Some(acc)  => processUserAuthContext(acc)
      case None       => orgLoginRepository.validateOrganisationUser(credentials) flatMap {
        case Some(acc)  => processOrgAuthContext(acc)
        case None       => Future.successful(None)
      }
    }
  }

  def getContext(id : String) : Future[Option[CurrentUser]] = {
    contextRepository.fetchCurrentUser(id) map {
      Some(_)
    } recover {
      case _: CurrentUserNotFoundException => None
    }
  }

  private[services] def processUserAuthContext(acc : UserAccount) : Future[Option[CurrentUser]] = {
    val generatedAuthContext = generateUserAuthContext(acc)
    contextRepository.cacheCurrentUser(generatedAuthContext) map {
      case MongoSuccessCreate => Some(generatedAuthContext)
      case MongoFailedCreate  => None
    }
  }

  private[services] def processOrgAuthContext(acc: OrgAccount): Future[Option[CurrentUser]] = {
    val generatedAuthContext = generateOrgAuthContext(acc)
    contextRepository.cacheCurrentUser(generatedAuthContext) map {
      case MongoSuccessCreate => Some(generatedAuthContext)
      case MongoFailedCreate  => None
    }
  }

  def generateUserAuthContext(acc: UserAccount): CurrentUser = CurrentUser(
    contextId      = generateContextId,
    id             = acc.userId,
    orgDeversityId = None,
    credentialType = INDIVIDUAL,
    orgName        = None,
    firstName      = Some(acc.firstName),
    lastName       = Some(acc.lastName),
    role           = acc.deversityDetails.map(_.role),
    enrolments     = acc.enrolments.map(Json.toJson(_).as[JsObject])
  )

  def generateOrgAuthContext(acc: OrgAccount): CurrentUser = CurrentUser(
    contextId      = generateContextId,
    id             = acc.orgId,
    orgDeversityId = Some(acc.deversityId),
    credentialType = ORGANISATION,
    orgName        = Some(acc.orgName),
    firstName      = None,
    lastName       = None,
    role           = None,
    enrolments     = None
  )
}
