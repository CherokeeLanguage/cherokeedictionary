package net.cherokeedictionary.auth

//import grails.rest.Resource

//@Resource(uri='/userRole/$email', formats=['json', 'xml'])
class User {
	String username
	String password
	String firstName
	String lastName
	String email
	String facebook
	String twitter
	String youtube
	boolean enabled = true
	boolean accountExpired = false
	boolean accountLocked = false
	boolean passwordExpired = false

	static constraints = {
		password nullable: false, blank: false, password: true
		email nullable: false, blank: false, unique: true
	}
}
