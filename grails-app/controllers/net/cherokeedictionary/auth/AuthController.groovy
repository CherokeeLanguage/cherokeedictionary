package net.cherokeedictionary.auth

class AuthController {
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def create() {
        def user = new User(email: params.email, password: params.password)
        if (!user.save(flush: true)) {
            //send user back to the create user page with applicable errors
        }

        def role = Role.find("from Role where role=?", ['member'])

        def userRole = new UserRole(user: user, role: role)
        if (!user.save(flush:true)) {
            //this should never be an error.  But if there's an error we want to send messages back to the user on the crate page.
        }

        // here we want to send the user to the profile page and display their new profile
        respond userRole
    }

    def update() {
        //the profile page will send data through params (see above line 7) and this will look up the User object by id or email
        //  then update the fields.  Any errors that occur should not be saved (see lines 8) and also data validation on the front end
        //  as well as verification on this end so that when the services are up and running we'll be able to validate.
        //  in addition, the domain classes will have restrictions and validation via GORM - to be done at a later time
        //  but send back to the profile page and highlight errors.
    }
}