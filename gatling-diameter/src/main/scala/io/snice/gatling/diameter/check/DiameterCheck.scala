/*
 * Copyright 2011-2019 GatlingCorp (https://gatling.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.snice.gatling.diameter.check

import java.util
import java.util.{Map => JMap}

import io.gatling.commons.validation.Validation
import io.gatling.core.check.{Check, CheckMaterializer, CheckResult}
import io.gatling.core.session.Session
import io.snice.codecs.codec.diameter.DiameterAnswer

/**
 * This class serves as model for the Diameter-specific checks
 *
 * @param wrapped the underlying check
 * @param scope the part of the response this check targets
 */
final case class DiameterCheck2(wrapped: Check[DiameterAnswer], scope: DiameterCheckScope) extends Check[DiameterAnswer] {
  override def check(response: DiameterAnswer, session: Session, preparedCache: JMap[Any, Any]): Validation[CheckResult] =
    wrapped.check(response, session, preparedCache)
}

final case class DiameterCheck(wrapped: Check[DiameterAnswer]) extends Check[DiameterAnswer] {
  override def check(answer: DiameterAnswer, session: Session, preparedCache: util.Map[Any, Any]): Validation[CheckResult] =
      wrapped.check(answer, session, preparedCache)
}

abstract class DiameterCheckMaterializer[T, P](scope: DiameterCheckScope) extends CheckMaterializer[T, DiameterCheck2, DiameterAnswer, P](DiameterCheck2(_, scope))


