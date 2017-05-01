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

package models

import play.api.libs.json.Json
import services.IdService

case class AuthContext(contextId : String,
                       user : User,
                       basicDetailsUri : String,
                       enrolmentsUri : String,
                       settingsUri : String)

case class User(userId : String,
                firstName : String,
                lastName : String)

object AuthContext extends IdService {
  implicit val formatUser = Json.format[User]
  implicit val format = Json.format[AuthContext]

  def generate(userId : String, firstName : String, lastName : String) : AuthContext = {
    AuthContext(
      contextId        = generateContextId,
      user             = User(userId, firstName, lastName),
      basicDetailsUri  = s"/account/$userId/basic-details",
      enrolmentsUri    = s"/account/$userId/enrolments",
      settingsUri      = s"/account/$userId/settings"
    )
  }
}
