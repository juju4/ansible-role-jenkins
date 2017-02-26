#!groovy
import hudson.security.*
import jenkins.model.*

def instance = Jenkins.getInstance()
def hudsonRealm = new HudsonPrivateSecurityRealm(false)
def users = hudsonRealm.getAllUsers()
users_s = users.collect { it.toString() }
/* can conflict with jenkins initial setup:
   'Jenkins initial setup is required. An admin user has been created and a password generated.'
   will results in 'Admin user already exists' but not with the right password
*/

if ("{{ jenkins_admin_username }}" in users_s) {
    println "Admin user '{{ jenkins_admin_username }}' already exists"
} else {
    println "--> creating local admin user: {{ jenkins_admin_username }}"

    hudsonRealm.createAccount('{{ jenkins_admin_username }}', '{{ jenkins_admin_password }}')
    instance.setSecurityRealm(hudsonRealm)

    def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
    instance.setAuthorizationStrategy(strategy)
    instance.save()
}
