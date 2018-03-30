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

package models

import com.cjwwdev.json.TimeFormat
import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json._
import services.IdService

case class OrgAccount(orgId: String,
                      deversityId: String,
                      orgName: String,
                      initials: String,
                      orgUserName: String,
                      location: String,
                      orgEmail: String,
                      credentialType: String,
                      password: String,
                      createdAt: DateTime,
                      settings: Option[Map[String, String]])

object OrgAccount extends IdService with TimeFormat {
  implicit val standardFormat: OFormat[OrgAccount] = (
    (__ \ "orgId").format[String] and
    (__ \ "deversityId").format[String] and
    (__ \ "orgName").format[String] and
    (__ \ "initials").format[String] and
    (__ \ "orgUserName").format[String] and
    (__ \ "location").format[String] and
    (__ \ "orgEmail").format[String] and
    (__ \ "credentialType").format[String] and
    (__ \ "password").format[String] and
    (__ \ "createdAt").format[DateTime](dateTimeRead)(dateTimeWrite) and
    (__ \ "settings").formatNullable[Map[String, String]]
  )(OrgAccount.apply, unlift(OrgAccount.unapply))
}
