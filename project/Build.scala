import sbt._
import sbt.Keys._
import play.Project._

object Build extends Build {

  val appName = "httpbin"
  val appVersion = "1.0-SNAPSHOT"

  scalaVersion := "2.10.2"
  autoScalaLibrary := false

  val appResolvers = Seq(

  )

  lazy val main = play.Project(appName, appVersion)


  object Dependencies {

    object Akka {
      val actor       = "com.typesafe.akka"     %% "akka-actor"       % "2.2.1"
      val io          = "com.typesafe.akka"     %% "akka-io"          % "2.2.1"
      val remote      = "com.typesafe.akka"     %% "akka-remote"      % "2.2.1"
      val kernel      = "com.typesafe.akka"     %% "akka-kernel"      % "2.2.1"
    }

    object Typesafe {
      val config      = "com.typesafe"          % "config"            % "1.0"
    }

    object Db {
      val slick       = "com.typesafe.slick"    %% "slick"            % "1.0.0"
      val mysql       = "mysql"                 % "mysql-connector-java" % "5.1.13"
    }

    object Google {
      val protobuf    = "com.google.protobuf"     % "protobuf-java"     % "2.4.1"
      val gson        = "com.google.code.gson"    % "gson"              % "2.2.4"
    }

    object Apache {
      val io          = "commons-io"              % "commons-io"        % "2.4"
      val http        = "org.apache.httpcomponents" % "httpclient"      % "4.2.5"
      val httpcore    = "org.apache.httpcomponents" % "httpcore"        % "4.2.5"
      val lang        = "commons-lang"              % "commons-lang"    % "2.6"
    }

    object Play {
      val json        = "com.typesafe.play"                     %% "play-json" % "2.2"
    }

    object Etc {
      val jsoup     = "org.jsoup"               % "jsoup"             % "1.7.2"
      val slf4j     = "org.slf4j"               % "slf4j-nop"         % "1.6.4"
      val bouncy    = "org.bouncycastle"        % "bcprov-jdk14"      % "1.49"
      val ombok     = "org.projectlombok"       % "lombok"            % "0.11.4"
    }

    object ReactivMongo {
      val core      = "org.reactivemongo"     %% "reactivemongo" % "0.10.0-SNAPSHOT"
      val play      = "org.reactivemongo"     %% "play2-reactivemongo" % "0.10.0-SNAPSHOT"
    }

    val database = Seq(Db.slick, Db.mysql)
    val akka = Seq(Akka.actor, Akka.remote, Akka.kernel)
  }

}
