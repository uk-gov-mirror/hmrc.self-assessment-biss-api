/*
 * Copyright 2021 HM Revenue & Customs
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

package v1.models.errors

import play.api.libs.json.{Json, Writes}

case class MtdError(code: String, message: String)

object MtdError {
  implicit val writes: Writes[MtdError] = Json.writes[MtdError]
}

object NinoFormatError extends MtdError("FORMAT_NINO", "The provided NINO is invalid")

object TaxYearFormatError extends MtdError("FORMAT_TAX_YEAR", "The provided tax year is invalid")

object SelfEmploymentIdFormatError extends MtdError("FORMAT_SELF_EMPLOYMENT_ID", "The supplied self-employment ID format is invalid")

object TypeOfBusinessFormatError extends MtdError("FORMAT_TYPE_OF_BUSINESS", "The provided type of business is invalid")

object BusinessIdFormatError extends MtdError("FORMAT_BUSINESS_ID","The provided Business ID is invalid")

// Rule Errors
object RuleTaxYearNotSupportedError
  extends MtdError("RULE_TAX_YEAR_NOT_SUPPORTED", "Tax year not supported, because it precedes the earliest allowable tax year")

object RuleIncorrectOrEmptyBodyError extends MtdError("RULE_INCORRECT_OR_EMPTY_BODY_SUBMITTED", "An empty or non-matching body was submitted")

object RuleTaxYearRangeInvalidError
  extends MtdError("RULE_TAX_YEAR_RANGE_INVALID", "Tax year range invalid. A tax year range of one year is required")

object RuleSelfEmploymentIdError
  extends MtdError("RULE_SELF_EMPLOYMENT_ID", "A self-employment ID must be supplied for a self-employment business")

object RuleTypeOfBusinessError
  extends MtdError("RULE_TYPE_OF_BUSINESS", "A type of business must be supplied")

object RuleForeignBusinessIdError
  extends MtdError("RULE_BUSINESS_ID","A Business ID must be supplied for a foreign property business")

//Standard Errors
object NotFoundError extends MtdError("MATCHING_RESOURCE_NOT_FOUND", "Matching resource not found")

object DownstreamError extends MtdError("INTERNAL_SERVER_ERROR", "An internal server error occurred")

object BadRequestError extends MtdError("INVALID_REQUEST", "Invalid request")

object BVRError extends MtdError("BUSINESS_ERROR", "Business validation error")

object ServiceUnavailableError extends MtdError("SERVICE_UNAVAILABLE", "Internal server error")

//Authorisation Errors
object UnauthorisedError extends MtdError("CLIENT_OR_AGENT_NOT_AUTHORISED", "The client and/or agent is not authorised")
object InvalidBearerTokenError extends MtdError("UNAUTHORIZED", "Bearer token is missing or not authorized")

// Accept header Errors
object  InvalidAcceptHeaderError extends MtdError("ACCEPT_HEADER_INVALID", "The accept header is missing or invalid")

object  UnsupportedVersionError extends MtdError("NOT_FOUND", "The requested resource could not be found")

object InvalidBodyTypeError extends MtdError("INVALID_BODY_TYPE", "Expecting text/json or application/json body")
