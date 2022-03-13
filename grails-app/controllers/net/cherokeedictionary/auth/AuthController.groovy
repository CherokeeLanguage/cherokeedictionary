package net.cherokeedictionary.auth

import net.cherokeedictionary.auth.User
import net.cherokeedictionary.auth.Role
import net.cherokeedictionary.auth.UserRole

class AuthController {
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def create(String email, String password) {
        def user = new User(email: email, password: password)
        user.save()
        def role = Role.find("from Role where role=?", ['member'])
        def userRole = new UserRole(user, role)

        respond userRole
    }

    def update() {

    }
}