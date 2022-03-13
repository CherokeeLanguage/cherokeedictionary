package net.cherokeedictionary.auth

class Role {
	String authority

	static constraints = {
		authority nullable: false, blank: false, unique: true
	}
}
