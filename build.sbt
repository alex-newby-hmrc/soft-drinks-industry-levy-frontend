// ================================================================================
// Plugins
// ================================================================================

enablePlugins(
  play.sbt.PlayScala,
  SbtAutoBuildPlugin,
  SbtGitVersioning,
  SbtDistributablesPlugin,
  SbtArtifactory
)

// ================================================================================
// Play configuration
// ================================================================================
TwirlKeys.templateImports ++= Seq("uk.gov.hmrc.uniform.playutil._", "sdil.utility._")
PlayKeys.playDefaultPort := 8700
// concatenate js
Concat.groups := Seq(
  "javascripts/sdil-frontend-app.js" -> group(Seq(
    "javascripts/application.js",
    "javascripts/timeout-dialog.js",
    "javascripts/show-hide-content.js",
    "javascripts/details.polyfill.js"
  ))
)

// force asset pipeline to operate in dev rather than only prod
pipelineStages in Assets := Seq(concat)

// ================================================================================
// Testing
// ================================================================================
import scoverage.ScoverageKeys._
import com.lucidchart.sbt.scalafmt.ScalafmtCorePlugin.autoImport._
coverageExcludedPackages := Seq(
  "app.*",
  "views.*",
  "uk.gov.hmrc.*",
  "prod.*",
  "sdil.config.*",
  "sdil.connectors.*",
  "sdil.models.*",
  "sdil.controllers.Routes",
  "sdil.controllers.SdilWMController",
  "sdil.filters.*",
  "controllers.javascript.*",
  "sdil.controllers.javascript.*",
  "sdil.controllers.RoutesPrefix",
  "testOnlyDoNotUseInAppConf.*",
  "sdil.controllers.test.*",
  "sdil.connectors.TestConnector",
  "sdil.forms",
  "variations.Routes"
).mkString(";")
coverageExcludedFiles := "<empty>;.*BuildInfo.*;.*Routes.*;.*GDS.*;.*GdsComponents.*;.*WebMonadPersistence.*;" +
  ".*SiteRef.*;.*ShowTitle.*;.*MoneyFormat.*;.*MappingWithExtraConstraint.*;.*AuthenticationController.*;"
coverageMinimum := 80
coverageFailOnMinimum := false
coverageHighlighting := true
scalafmtOnCompile in Compile := true
scalafmtOnCompile in Test := true

libraryDependencies ++= Seq(
  "org.scalatest"          %% "scalatest"          % "3.0.7",
  "org.pegdown"            % "pegdown"             % "1.6.0",
  "org.jsoup"              % "jsoup"               % "1.12.1",
  "com.typesafe.play"      %% "play-test"          % play.core.PlayVersion.current,
  "org.mockito"            % "mockito-core"        % "2.28.2",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2",
  "org.scalacheck"         %% "scalacheck"         % "1.14.0"
).map(_ % "test")

// ================================================================================
// Dependencies
// ================================================================================

scalaVersion := "2.12.0"

libraryDependencies ++= Seq(
  ws,
  "uk.gov.hmrc"               %% "bootstrap-play-26"              % "1.1.0",
  "uk.gov.hmrc"               %% "domain"                         % "5.6.0-play-26",  //see if can be removed after upgrade
  "uk.gov.hmrc"               %% "govuk-template"                 % "5.35.0-play-26",
  "uk.gov.hmrc"               %% "play-ui"                        % "8.3.0-play-26",
  "uk.gov.hmrc"               %% "play-partials"                  % "6.9.0-play-26",
  "com.typesafe.play"         %% "play-json"                      % "2.6.13",
  "org.scalactic"             %% "scalactic"                      % "3.0.5",
  "uk.gov.hmrc"               %% "auth-client"                    % "2.31.0-play-26",  //check version
  "uk.gov.hmrc"               %% "http-caching-client"            % "9.0.0-play-26",
  "uk.gov.hmrc"               %% "play-conditional-form-mapping"  % "1.1.0-play-26",
  "com.softwaremill.macwire"  %% "macros"                         % "2.3.1" % "provided",
  "com.softwaremill.macwire"  %% "macrosakka"                     % "2.3.1" % "provided",
  "com.softwaremill.macwire"  %% "util"                           % "2.3.1",
  "com.softwaremill.macwire"  %% "proxy"                          % "2.3.1",
  "org.typelevel"             %% "cats-core"                      % "1.1.0",  //Upgrade later to 1.6.0
  "uk.gov.hmrc"               %% "uniform"                        % "0.1.9" exclude("com.typesafe.play", "play-logback")  //maybe later
)

resolvers ++= Seq(
  Resolver.bintrayRepo("hmrc", "releases"),
  Resolver.jcenterRepo
)

// ================================================================================
// Compiler flags
// ================================================================================

scalacOptions ++= Seq(
//  "-Xfatal-warnings",                  // Fail the compilation if there are any warnings.
  "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
  "-encoding", "utf-8",                // Specify character encoding used by source files.
  "-explaintypes",                     // Explain type errors in more detail.
  "-feature",                          // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked",                        // Enable additional warnings where generated code depends on assumptions.
  "-Xcheckinit",                       // Wrap field accessors to throw an exception on uninitialized access.
  "-Xfuture",                          // Turn on future language features.
  "-Xlint:adapted-args",               // Warn if an argument list is modified to match the receiver.
  "-Xlint:by-name-right-associative",  // By-name parameter of right associative operator.
  "-Xlint:delayedinit-select",         // Selecting member of DelayedInit.
  "-Xlint:doc-detached",               // A Scaladoc comment appears to be detached from its element.
  "-Xlint:inaccessible",               // Warn about inaccessible types in method signatures.
  "-Xlint:infer-any",                  // Warn when a type argument is inferred to be `Any`.
  "-Xlint:missing-interpolator",       // A string literal appears to be missing an interpolator id.
  "-Xlint:nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Xlint:nullary-unit",               // Warn when nullary methods return Unit.
  "-Xlint:option-implicit",            // Option.apply used implicit view.
  "-Xlint:package-object-classes",     // Class or object defined in package object.
  "-Xlint:poly-implicit-overload",     // Parameterized overloaded implicit methods are not visible as view bounds.
  "-Xlint:private-shadow",             // A private field (or class parameter) shadows a superclass field.
  "-Xlint:stars-align",                // Pattern sequence wildcard must align with sequence component.
  "-Xlint:type-parameter-shadow",      // A local type parameter shadows a type already in scope.
  "-Xlint:unsound-match",              // Pattern match may not be typesafe.
  "-Yno-adapted-args",                 // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
  "-Ywarn-dead-code",                  // Warn when dead code is identified.
  "-Ywarn-inaccessible",               // Warn about inaccessible types in method signatures.
  "-Ywarn-infer-any",                  // Warn when a type argument is inferred to be `Any`.
  "-Ywarn-nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Ywarn-nullary-unit",               // Warn when nullary methods return Unit.
  "-Ywarn-numeric-widen",              // Warn when numerics are widened.
  "-Ywarn-unused",                     // Warn if an import selector is not referenced.
  "-Ywarn-value-discard"               // Warn when non-Unit expression results are unused.
)

// ================================================================================
// Misc
// ================================================================================

initialCommands in console := "import cats.implicits._"

majorVersion := 0

uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings
