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

trait IntegrationStubbing {
  self: IntegrationSpec =>

  class PreconditionBuilder {
    implicit val builder: PreconditionBuilder = this

    def currentUsers: CurrentUsers         = CurrentUsers()
    def individualUser: IndividualUser     = IndividualUser()
    def organisationUser: OrganisationUser = OrganisationUser()
  }

  def given: PreconditionBuilder = new PreconditionBuilder

  case class CurrentUsers()(implicit builder: PreconditionBuilder) {
    def hasCurrentUser: PreconditionBuilder = {
      await(contextRepository.collection.flatMap(_.insert(testCurrentUser)))
      builder
    }
  }

  case class IndividualUser()(implicit builder: PreconditionBuilder) {
    def isSetup: PreconditionBuilder = {
      await(loginRepository.collection.flatMap(_.insert(testAccount)))
      builder
    }
  }

  case class OrganisationUser()(implicit builder: PreconditionBuilder) {
    def isSetup: PreconditionBuilder = {
      await(orgLoginRepository.collection.flatMap(_.insert(testOrgAccount)))
      builder
    }
  }
}
