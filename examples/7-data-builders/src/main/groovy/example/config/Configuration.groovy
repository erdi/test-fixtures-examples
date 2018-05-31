package example.config

class Configuration extends io.dropwizard.Configuration {

    boolean remoteControlEnabled = false

    DbConfiguration db = new DbConfiguration()

}
