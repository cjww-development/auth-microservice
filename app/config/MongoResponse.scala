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
package config

sealed trait MongoResponse
case object MongoSuccessCreate extends MongoResponse
case object MongoFailedCreate extends MongoResponse

case class MongoSuccessRead(data : Any) extends MongoResponse
case object MongoFailedRead extends MongoResponse

case object MongoSuccessUpdate extends MongoResponse
case object MongoFailedUpdate extends MongoResponse

case object MongoSuccessDelete extends MongoResponse
case object MongoFailedDelete extends MongoResponse
