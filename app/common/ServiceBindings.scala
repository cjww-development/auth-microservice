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

package common

import com.cjwwdev.config.{ConfigurationLoader, ConfigurationLoaderImpl}
import com.google.inject.AbstractModule
import repositories._
import services._
import controllers._

class ServiceBindings extends AbstractModule {
  override def configure(): Unit = {
    bindOther()
    bindRepositories()
    bindServices()
    bindControllers()
  }

  private def bindRepositories(): Unit = {
    bind(classOf[ContextRepository]).to(classOf[ContextRepositoryImpl]).asEagerSingleton()
    bind(classOf[LoginRepository]).to(classOf[LoginRepositoryImpl]).asEagerSingleton()
    bind(classOf[OrgLoginRepository]).to(classOf[OrgLoginRepositoryImpl]).asEagerSingleton()
  }

  private def bindServices(): Unit = {
    bind(classOf[LoginService]).to(classOf[LoginServiceImpl]).asEagerSingleton()
  }

  private def bindControllers(): Unit = {
    bind(classOf[LoginController]).to(classOf[LoginControllerImpl]).asEagerSingleton()
  }

  private def bindOther(): Unit = {
    bind(classOf[ConfigurationLoader]).to(classOf[ConfigurationLoaderImpl]).asEagerSingleton()
  }
}
