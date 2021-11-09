sealed class BuildType {



  object IntegrationTest : BuildType() {

  }

  object HandTest : BuildType() {

  }

  object Release : BuildType() {

  }

  object Debug : BuildType() {

  }

  class UseLocal(val path: String) : BuildType() {

  }

}
