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

import com.cjwwdev.security.deobfuscation.{DeObfuscation, DeObfuscator, DecryptionError}
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Login(username : String, password : String)

object Login {
  implicit val standardFormat: OFormat[Login] = (
    (__ \ "username").format[String] and
    (__ \ "password").format[String]
  )(Login.apply, unlift(Login.unapply))

  implicit val deObfuscator: DeObfuscator[Login] = new DeObfuscator[Login] {
    override def decrypt(value: String): Either[Login, DecryptionError] = DeObfuscation.deObfuscate[Login](value)
  }
}
