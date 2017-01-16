package com.twitter.finagle.http

import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.{Base64StringEncoder, Future}
import scala.util.control.NoStackTrace

object BasicAuth {
  private val WwwAuthenticate = "Basic (.*)".r

  private object Credentials {
    private[this] val UsrAndPasswd = "(.*):(.*)".r

    def apply(username: String, password: String): String =
      Base64StringEncoder.encode(s"$username:$password".getBytes("UTF-8"))

    def unapply(s: String): Option[(String, String)] =
      new String(Base64StringEncoder.decode(s), "UTF-8") match {
        case UsrAndPasswd(u, p) => Some(u -> p)
        case _ => None
      }
  }

  final class Server(authenticate: (String, String) => Future[Boolean]) extends SimpleFilter[Request, Response] {
    private[this] def authenticated(req: Request): Future[Boolean] = 
      req.authorization match {
        case Some(BasicAuth.WwwAuthenticate(BasicAuth.Credentials(u, p))) => authenticate(u, p)
        case _ => Future.False
      }

    def apply(req: Request, s: Service[Request, Response]): Future[Response] =
      authenticated(req).flatMap {
        case true => s(req)
        case false => Future.value(Response(Status.Unauthorized))
      }
  }

  final class Client(username: String, password: String) extends SimpleFilter[Request, Response] {
    def apply(req: Request, s: Service[Request, Response]): Future[Response] = {
      req.authorization = s"Basic ${Credentials(username, password)}"
      s(req)
    }
  }

  def server(authenticate: (String, String) => Future[Boolean]): BasicAuth.Server =
    new BasicAuth.Server(authenticate)

  def serverFromCredentials(username: String, password: String): BasicAuth.Server =
    new BasicAuth.Server((u, p) => if (u == username && p == password) Future.True else Future.False)

  def client(username: String, password: String): BasicAuth.Client =
    new BasicAuth.Client(username, password)
}