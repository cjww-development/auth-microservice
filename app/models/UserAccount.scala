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

case class Enrolments(hubId : Option[String],
                      diagId : Option[String],
                      deversityId : Option[String])

object Enrolments {
  implicit val standardFormat: OFormat[Enrolments] = (
    (__ \ "hubId").formatNullable[String] and
    (__ \ "diagId").formatNullable[String] and
    (__ \ "deversityId").formatNullable[String]
  )(Enrolments.apply, unlift(Enrolments.unapply))
}

case class DeversityEnrolment(schoolDevId: String,
                              role: String,
                              title: Option[String],
                              room: Option[String],
                              teacher: Option[String])

object DeversityEnrolment {
  implicit val standardFormat: OFormat[DeversityEnrolment] = (
    (__ \ "schoolDevId").format[String] and
    (__ \ "role").format[String] and
    (__ \ "title").formatNullable[String] and
    (__ \ "room").formatNullable[String] and
    (__ \ "teacher").formatNullable[String]
  )(DeversityEnrolment.apply, unlift(DeversityEnrolment.unapply))
}

case class UserAccount(userId : String,
                       firstName : String,
                       lastName : String,
                       userName : String,
                       email : String,
                       password : String,
                       deversityDetails: Option[DeversityEnrolment],
                       createdAt : DateTime,
                       enrolments: Option[Enrolments] = None,
                       settings : Option[Map[String, String]] = None)

object UserAccount extends IdService with TimeFormat {
  implicit val standardFormat: OFormat[UserAccount] = (
    (__ \ "userId").format[String] and
    (__ \ "firstName").format[String] and
    (__ \ "lastName").format[String] and
    (__ \ "userName").format[String] and
    (__ \ "email").format[String] and
    (__ \ "password").format[String] and
    (__ \ "deversityDetails").formatNullable[DeversityEnrolment] and
    (__ \ "createdAt").format[DateTime](dateTimeRead)(dateTimeWrite) and
    (__ \ "enrolments").formatNullable[Enrolments] and
    (__ \ "settings").formatNullable[Map[String, String]]
  )(UserAccount.apply, unlift(UserAccount.unapply))
}
