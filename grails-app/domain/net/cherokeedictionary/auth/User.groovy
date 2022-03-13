package net.cherokeedictionary.auth

class User {
	String username
	String password
	String fullName
	String email
	Stirng uuid
	string facebook
	String twitter
	String youtube

	static constraints = {
		password nullable: false, blank: false, password: true
		email nullable: false, blank: false, unique: true
	}
}
