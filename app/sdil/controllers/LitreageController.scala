/*
 * Copyright 2017 HM Revenue & Customs
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

package sdil.controllers

import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import sdil.actions.FormAction
import sdil.config.AppConfig
import sdil.forms.FormHelpers
import sdil.models._
import uk.gov.hmrc.http.cache.client.SessionCache
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.play.bootstrap.http.FrontendErrorHandler
import views.html.softdrinksindustrylevy.register.litreagePage

import scala.util.Try

class LitreageController(val messagesApi: MessagesApi,
                         errorHandler: FrontendErrorHandler,
                         cache: SessionCache,
                         formAction: FormAction)
                        (implicit config: AppConfig)
  extends FrontendController with I18nSupport {

  import LitreageController._

  def show(pageName: String) = formAction.async { implicit request =>
    val page = getPage(pageName)

    page.expectedPage(request.formData) match {
      case `page` => Ok(litreagePage(form, pageName, page.previousPage(request.formData).show))
      case otherPage => Redirect(otherPage.show)
    }
  }

  def validate(pageName: String) = formAction.async { implicit request =>
    val page = getPage(pageName)
    form.bindFromRequest().fold(
      errors => BadRequest(litreagePage(errors, pageName, page.nextPage(request.formData).show)),
      litreage => {
        val updated = update(litreage, request.formData, page)
        cache.cache("formData", updated) map { _ =>
          Redirect(page.nextPage(updated).show)
        }
      }
    )
  }

  private def getPage(pageName: String): MidJourneyPage = pageName match {
    case "packageOwn" => PackageOwnPage
    case "packageCopack" => PackageCopackPage
    case "packageCopackSmallVol" => PackageCopackSmallVolPage
    case "copackedVolume" => CopackedVolumePage
    case "importVolume" => ImportVolumePage
    case other => throw new IllegalArgumentException(s"Invalid page litreage page: $other")
  }

  private def update(litreage: Litreage, formData: RegistrationFormData, page: Page): RegistrationFormData = page match {
    case PackageOwnPage => formData.copy(packageOwn = Some(litreage))
    case PackageCopackPage => formData.copy(packageCopack = Some(litreage))
    case PackageCopackSmallVolPage => formData.copy(packageCopackSmallVol = Some(litreage))
    case CopackedVolumePage => formData.copy(copackedVolume = Some(litreage))
    case ImportVolumePage => formData.copy(importVolume = Some(litreage))
  }
}

object LitreageController extends FormHelpers {
  val form: Form[Litreage] = Form(
    mapping(
      "lowerRateLitres" -> litreage,
      "higherRateLitres" -> litreage
    )(Litreage.apply)(Litreage.unapply))

  private lazy val litreage = text
    .verifying("error.litreage.required", _.nonEmpty)
    .verifying("error.litreage.numeric", l => l.isEmpty || Try(l.toLong).isSuccess) //don't try to parse empty string as a number
    .transform[Long](_.toLong, _.toString)
    .verifying("error.litreage.max", _ <= 9999999999999L)
    .verifying("error.litreage.min", _ >= 0)
}
