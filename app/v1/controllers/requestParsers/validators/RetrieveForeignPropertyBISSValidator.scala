/*
 * Copyright 2020 HM Revenue & Customs
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

package v1.controllers.requestParsers.validators

import v1.controllers.requestParsers.validators.validations.{BusinessIdValidation, ForeignTypeOfBusinessValidation, NinoValidation, TaxYearValidation}
import v1.models.errors.MtdError
import v1.models.requestData.RetrieveForeignPropertyBISSRawData

class RetrieveForeignPropertyBISSValidator extends Validator[RetrieveForeignPropertyBISSRawData]{

  private val validationSet = List(parameterFormatValidation)

  private def parameterFormatValidation : RetrieveForeignPropertyBISSRawData => List[List[MtdError]] = (data: RetrieveForeignPropertyBISSRawData) => List(
    NinoValidation.validate(data.nino),
    data.taxYear.map(TaxYearValidation.validate).getOrElse(Nil),
    BusinessIdValidation.validate(data.businessId.get),
    ForeignTypeOfBusinessValidation.validate(data.typeOfBusiness)
  )

  override def validate(data: RetrieveForeignPropertyBISSRawData): List[MtdError] = run(validationSet, data).distinct

}
