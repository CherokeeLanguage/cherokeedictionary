package net.cherokeedictionary.auth

class AuthController {
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def register() {
        if (params.nosave) {
            render(view: '/auth/register', model: [nosave: params.nosave, username: params.username])
        }
    }

    def create() {
        def user = new User(username: params.username, password: params.password)
        if (!user.save(flush: true)) {
            //send user back to the create user page with applicable errors
            redirect(uri: "/auth/register", params: [nosave: "user not created", username: params.username])
            return
        }

        def role = Role.findByAuthority('member')

        def userRole = new UserRole(user: user, role: role)
        if (!userRole.save(flush:true)) {
            //this should never be an error.  But if there's an error we want to send messages back to the user on the crate page.
            redirect(uri: "/auth/register", params: [nosave: "userRole not created"])
            return
        }

        // here we want to send the user to the profile page and display their new profile
        redirect(uri: "/auth/profile", params: [todisplay: user])
        return
    }

    def update() {
        //the profile page will send data through params (see above line 7) and this will look up the User object by id or email
        //  then update the fields.  Any errors that occur should not be saved (see lines 8) and also data validation on the front end
        //  as well as verification on this end so that when the services are up and running we'll be able to validate.
        //  in addition, the domain classes will have restrictions and validation via GORM - to be done at a later time
        //  but send back to the profile page and highlight errors.

        def user = User.findByEmail(params.email)
        def errors = [:]

        if (!params.username) {
            errors.username = "username not defined"
        } else {
            user.username = params.username
        }

        if (!params.firstName) {
            errors.firstName = "firstName not defined"
        } else {
            user.firstName = params.firstName
        }

        if (!params.lastName) {
            errors.lastName = "lastName not defined"
        } else {
            user.lastName = params.lastName
        }

        if (!params.facebook) {
            errors.lastName = "facebook not defined"
        } else {
            user.facebook = params.facebook
        }

        if (!params.twitter) {
            errors.lastName = "twitter not defined"
        } else {
            user.twitter = params.twitter
        }

        if (!params.youtube) {
            errors.lastName = "youtube not defined"
        } else {
            user.youtube = params.youtube
        }

        if (!user.save(flush: true)) {
            redirect(uri: "/update", params: [nosave: "user not updated"])
        }

        //password and email updating not implemented yet

        redirect(uri: "/profile", params: [todisplay: user, errors: errors])
    }

    def profile() {
        def user = User.findByEmail(params.email)
        def userRole = UserRole.findByUser(user)
        render(view: "/profile", model: [todisplay: user, userRole: userRole, errors: params.errors])
    }
}