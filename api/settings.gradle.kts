rootProject.name = "ribbit-api"

include("core")
include("posts")
include("users")
include("subs")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("http4k", "5.1.1.1")
            version("forkhandles", "2.6.0.0")
            version("http4k-connect", "5.0.0.0")
            version("kotest", "5.6.2")
            version("slf4j", "2.0.7")

            // http4k
            library("http4k-core", "org.http4k", "http4k-core").versionRef("http4k")
            library("http4k-contract", "org.http4k", "http4k-contract").versionRef("http4k")
            library("http4k-format-moshi", "org.http4k", "http4k-format-moshi").versionRef("http4k")
            library("http4k-format-argo", "org.http4k", "http4k-format-argo").versionRef("http4k")
            library("http4k-format-kondor", "org.http4k", "http4k-format-kondor-json").versionRef("http4k")
            library("http4k-cloudnative", "org.http4k", "http4k-cloudnative").versionRef("http4k")
            library("http4k-serverless-lambda", "org.http4k", "http4k-serverless-lambda").versionRef("http4k")
            library("http4k-testing-kotest", "org.http4k", "http4k-testing-kotest").versionRef("http4k")
            library("http4k-testing-approval", "org.http4k", "http4k-testing-approval").versionRef("http4k")

            //http4k-connect
            library("http4k-connect-amazon-dynamodb", "org.http4k", "http4k-connect-amazon-dynamodb").versionRef("http4k-connect")
            library("http4k-connect-amazon-dynamodb-fake", "org.http4k", "http4k-connect-amazon-dynamodb-fake").versionRef("http4k-connect")
            library("http4k-connect-amazon-kms", "org.http4k", "http4k-connect-amazon-kms").versionRef("http4k-connect")
            library("http4k-connect-amazon-kms-fake", "org.http4k", "http4k-connect-amazon-kms-fake").versionRef("http4k-connect")

            // forkhandles
            library("forkhandles-time4k", "dev.forkhandles", "time4k").versionRef("forkhandles")
            library("forkhandles-result4k", "dev.forkhandles", "result4k").versionRef("forkhandles")
            library("forkhandles-result4k-kotest", "dev.forkhandles", "result4k-kotest").versionRef("forkhandles")
            library("forkhandles-values4k", "dev.forkhandles", "values4k").versionRef("forkhandles")

            // kotest
            library("kotest-assertions-core", "io.kotest", "kotest-assertions-core-jvm").versionRef("kotest")

            // slf4j
            library("slf4j-api", "org.slf4j", "slf4j-api").versionRef("slf4j")
            library("slf4j-simple", "org.slf4j", "slf4j-simple").versionRef("slf4j")

            // misc
            library("utils", "com.github.oharaandrew314", "service-utils").version("1.3.1")
            library("nimbus", "com.nimbusds", "nimbus-jose-jwt").version("9.26")
            library("nimbus-kms", "com.github.oharaandrew314", "nimbus-kms").version("c32275e584")
        }
    }
}