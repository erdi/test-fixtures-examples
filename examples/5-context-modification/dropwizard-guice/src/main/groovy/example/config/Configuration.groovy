package example.config

import com.google.inject.Module

class Configuration extends io.dropwizard.Configuration {

    SmtpConfiguration smtp = new SmtpConfiguration()
    DbConfiguration db = new DbConfiguration()
    List<Module> overridingModules = []

}
