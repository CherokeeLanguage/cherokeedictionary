package net.cherokeedictionary.auth

//import grails.rest.Resource

//@Resource(uri='/userRole/$userid', formats=['json', 'xml'])
class UserRole {
	User user
	Role role

	static mapping = {
		id composite: ['user', 'role']
		version false
	}
}
