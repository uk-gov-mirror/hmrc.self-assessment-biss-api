package v1.connectors

import config.AppConfig
import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import v1.models.requestData.RetrieveForeignPropertyBissRequest
import v1.models.response.RetrieveForeignPropertyBISSResponse

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ForeignPropertyBissConnector @Inject()(val http: HttpClient,
                                             val appConfig: AppConfig) extends BaseDesConnector {

  def retrieveBiss(request: RetrieveForeignPropertyBissRequest)(
    implicit hc: HeaderCarrier,
    ec: ExecutionContext): Future[DesOutcome[RetrieveForeignPropertyBISSResponse]] = {

    val nino = request.nino
    val businessId = request.businessId
    val typeOfBusiness = request.typeOfBusiness.toString
    val taxYear = request.taxYear.toString

    get(
      DesUri[RetrieveForeignPropertyBISSResponse](
        s"/income-tax/income-sources/nino/$nino/$typeOfBusiness/$taxYear/biss?incomesourceid=$businessId")
    )
  }

}
