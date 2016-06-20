package controllers

import play.api.mvc.{Controller, Action}

import scala.concurrent.Future

trait BasicAuthenticationController extends Controller{

  def BasicAuthentication[A](action: Action[A]): Action[A] = Action.async(action.parser) { request =>
    request.headers.get("Authorization").flatMap { authorization =>

      authorization.split(" ").drop(1).headOption.filter { encoded =>
        new String(org.apache.commons.codec.binary.Base64.decodeBase64(encoded.getBytes)).split(":").toList match {
          case username :: password :: Nil if username == "test@example.com" && password == "test123456" => true   // Here you can perform your user authentication process from RDS
          case _                                                                                         => false
        }
      }
    }.map(_ => action(request)).getOrElse {
      Future.successful(Unauthorized("Authentication Failed"))
    }
  }
}
