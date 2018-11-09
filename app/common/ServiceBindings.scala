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

package common

import com.cjwwdev.config.{ConfigurationLoader, DefaultConfigurationLoader}
import com.cjwwdev.featuremanagement.models.Features
import com.cjwwdev.health.{DefaultHealthController, HealthController}
import com.cjwwdev.mongo.indexes.RepositoryIndexer
import controllers._
import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment}
import repositories._
import services._

class ServiceBindings extends Module {

  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] =
    bindOther() ++ bindRepositories() ++ bindServices() ++ bindControllers()

  private def bindRepositories(): Seq[Binding[_]] = Seq(
    bind(classOf[ContextRepository]).to(classOf[DefaultContextRepository]).eagerly(),
    bind(classOf[LoginRepository]).to(classOf[DefaultLoginRepository]).eagerly(),
    bind(classOf[OrgLoginRepository]).to(classOf[DefaultOrgLoginRepository]).eagerly(),
    bind(classOf[RepositoryIndexer]).to(classOf[AuthIndexer]).eagerly()
  )

  private def bindServices(): Seq[Binding[_]] = Seq(
    bind(classOf[LoginService]).to(classOf[DefaultLoginService]).eagerly()
  )

  private def bindControllers(): Seq[Binding[_]] = Seq(
    bind(classOf[LoginController]).to(classOf[DefaultLoginController]).eagerly(),
    bind(classOf[HealthController]).to(classOf[DefaultHealthController]).eagerly()
  )

  private def bindOther(): Seq[Binding[_]] = Seq(
    bind(classOf[ConfigurationLoader]).to(classOf[DefaultConfigurationLoader]).eagerly(),
    bind(classOf[Features]).to(classOf[FeatureDef]).eagerly()
  )
}
