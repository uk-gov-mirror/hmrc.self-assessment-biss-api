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

package v1.controllers

import fixtures.RetrieveSelfEmploymentBISSFixture._
import play.api.libs.json.Json
import play.api.mvc.Result
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import v1.mocks.MockIdGenerator
import v1.mocks.requestParsers.MockRetrieveSelfEmploymentBISSRequestDataParser
import v1.mocks.services.{MockEnrolmentsAuthService, MockMtdIdLookupService, MockSelfEmploymentBISSService}
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.models.requestData.{DesTaxYear, RetrieveSelfEmploymentBISSRawData, RetrieveSelfEmploymentBISSRequest}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrieveSelfEmploymentBISSControllerSpec
  extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockRetrieveSelfEmploymentBISSRequestDataParser
    with MockSelfEmploymentBISSService
    with MockIdGenerator {

  private val nino = "AA123456A"
  private val taxYear = Some("2018-19")
  private val selfEmploymentId = "123456789"
  private val correlationId = "X-123"

  trait Test {
    val hc: HeaderCarrier = HeaderCarrier()

    val controller = new RetrieveSelfEmploymentBISSController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      requestParser = mockRequestParser,
      selfEmploymentBISSService = mockService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockedEnrolmentsAuthService.authoriseUser()
    MockIdGenerator.generateCorrelationId.returns(correlationId)
  }

  private val rawData = RetrieveSelfEmploymentBISSRawData(nino, taxYear, selfEmploymentId)
  private val requestData = RetrieveSelfEmploymentBISSRequest(Nino(nino), DesTaxYear("2019"), selfEmploymentId)

  "retrieveBiss" should {
    "return successful response with status OK" when {
      "a valid response received" in new Test {

        MockRetrieveSelfEmploymentBISSRequestDataParser
          .parse(rawData)
          .returns(Right(requestData))

        MockSelfEmploymentBISSService
          .retrieveBiss(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseObj))))

        val result: Future[Result] = controller.retrieveBiss(nino, selfEmploymentId, taxYear)(fakeGetRequest)

        status(result) shouldBe OK
        contentAsJson(result) shouldBe mtdResponse
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }

    "return the error as per spec" when {
      "parser errors occur" must {
        def errorsFromParserTester(error: MtdError, expectedStatus: Int): Unit = {
          s"a ${error.code} error is returned from the parser" in new Test {

            MockRetrieveSelfEmploymentBISSRequestDataParser
              .parse(rawData)
              .returns(Left(ErrorWrapper(correlationId, error, None)))

            val result: Future[Result] = controller.retrieveBiss(nino, selfEmploymentId, taxYear)(fakeGetRequest)

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(error)
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val input = Seq(
          (BadRequestError, BAD_REQUEST),
          (NinoFormatError, BAD_REQUEST),
          (TaxYearFormatError, BAD_REQUEST),
          (SelfEmploymentIdFormatError, BAD_REQUEST),
          (DownstreamError, INTERNAL_SERVER_ERROR)
        )

        input.foreach(args => (errorsFromParserTester _).tupled(args))
      }

      "service errors occur" must {
        def serviceErrors(mtdError: MtdError, expectedStatus: Int): Unit = {
          s"a $mtdError error is returned from the service" in new Test {

            MockRetrieveSelfEmploymentBISSRequestDataParser
              .parse(rawData)
              .returns(Right(requestData))

            MockSelfEmploymentBISSService
              .retrieveBiss(requestData)
              .returns(Future.successful(Left(ErrorWrapper(correlationId, mtdError))))

            val result: Future[Result] = controller.retrieveBiss(nino, selfEmploymentId, taxYear)(fakeGetRequest)

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(mtdError)
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val input = Seq(
          (NinoFormatError, BAD_REQUEST),
          (TaxYearFormatError, BAD_REQUEST),
          (SelfEmploymentIdFormatError, BAD_REQUEST),
          (NotFoundError, NOT_FOUND),
          (DownstreamError, INTERNAL_SERVER_ERROR),
          (RuleTypeOfBusinessError, BAD_REQUEST)
        )

        input.foreach(args => (serviceErrors _).tupled(args))
      }
    }
  }
}