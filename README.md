[![Build Status](https://travis-ci.org/finagle/finagle-http-auth.svg?branch=master)](https://travis-ci.org/finagle/finagle-http-auth)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.finagle/finagle-http-auth_2.11.svg)](https://maven-badges.herokuapp.com/maven-central/com/github.finagle/finagle-http-auth_2.11)

Implementing HTTP Basic Auth as a Finagle filters for both clients and servers.

# Usage

Starting an HTTP server with Basic Auth filter applied.

```scala
scala> import com.twitter.finagle.Http, com.twitter.finagle.http.{BasicAuth, Request, Response}, com.twitter.finagle.Service, com.twitter.util.Future
import com.twitter.finagle.Http
import com.twitter.finagle.http.{BasicAuth, Request, Response}
import com.twitter.finagle.Service
import com.twitter.util.Future

scala> val s = new Service[Request, Response] { def apply(req: Request): Future[Response] = Future.value(Response()) }
s: com.twitter.finagle.Service[com.twitter.finagle.http.Request,com.twitter.finagle.http.Response] = <function1>

scala> val ba = BasicAuth.serverFromCredentials("admin", "12345")
ba: com.twitter.finagle.http.BasicAuth.Server = <function2>


scala> Http.server.serve(":8081", ba.andThen(s))
res2: com.twitter.finagle.ListeningServer = Group(/0:0:0:0:0:0:0:0:8081)
```

Starting an HTTP client along with two Basic Auth filters indicating wrong and correct credentials.

```scala
import com.twitter.finagle.Http
import com.twitter.finagle.http.{BasicAuth, Request, Response}
import com.twitter.finagle.Service
import com.twitter.util.{Await, Future}

scala> val good = BasicAuth.client("admin", "12345")
good: com.twitter.finagle.http.BasicAuth.Client = <function2>

scala> val bad = BasicAuth.client("root", "deadbeef")
bad: com.twitter.finagle.http.BasicAuth.Client = <function2>

scala> val c = Http.client.newService("localhost:8081")
Jan 15, 2017 4:28:43 PM com.twitter.finagle.Init$$anonfun$4 apply$mcV$sp
INFO: Finagle version 6.41.0 (rev=95eedf5f41f78414fae25d93cc8fae02eeb5a75d) built at 20161220-164342
c: com.twitter.finagle.Service[com.twitter.finagle.http.Request,com.twitter.finagle.http.Response] = <function1>

scala> Await.result(good.andThen(c)(Request()))
res0: com.twitter.finagle.http.Response = Response("HTTP/1.1 Status(200)")

scala> Await.result(bad.andThen(c)(Request()))
res1: com.twitter.finagle.http.Response = Response("HTTP/1.1 Status(401)")
```
## License

finagle-http-auth is licensed under the **[Apache License, Version 2.0][apache]**
(the "License"); you may not use this software except in compliance with the
License.

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

[apache]: http://www.apache.org/licenses/LICENSE-2.0