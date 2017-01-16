package com.twitter.finagle.http

import com.twitter.finagle.Service
import com.twitter.util.{Await, Base64StringEncoder, Future}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class BasicAuthSuite extends FunSuite with GeneratorDrivenPropertyChecks {

  val ok = new Service[Request, Response] {
    def apply(req: Request): Future[Response] = Future.value(Response())
  }

  test("success") {
    forAll { (u: String, p: String) =>
      val c = BasicAuth.client(u, p)
      val s = BasicAuth.serverFromCredentials(u, p)
      val all = c.andThen(s).andThen(ok)

      assert(Await.result(all(Request())).status == Status.Ok)
    }
  }

  test("failure (missing credentials)") {
    val s = BasicAuth.serverFromCredentials("usr", "passwd")
    val all = s.andThen(ok)

    assert(Await.result(all(Request())).status == Status.Unauthorized)
  }

  test("failure (wrong credentials)") {
    forAll { (u: String, p: String) =>
      val c = BasicAuth.client(s"WRONG $u", "WRONG $p")
      val s = BasicAuth.serverFromCredentials(u, p)
      val all = c.andThen(s).andThen(ok)

      assert(Await.result(all(Request())).status == Status.Unauthorized)
    }
  }

  test("failure (wrong header format)") {
    forAll { (u: String, p: String) =>
      val s = BasicAuth.serverFromCredentials(u, p)
      val all = s.andThen(ok)
      val req = Request()
      val bytes = s"$u:$p".getBytes("UTF-8")
      req.authorization = s"Bazik ${Base64StringEncoder.encode(bytes)}"

      assert(Await.result(all(req)).status == Status.Unauthorized)
    }
  }
}