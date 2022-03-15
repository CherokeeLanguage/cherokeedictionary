package net.cherokeedictionary.auth

//import grails.rest.Resource

//@Resource(uri='/role/$roleId', formats=['json', 'xml'])
class Role {
	String authority

	static constraints = {
		authority nullable: false, blank: false, unique: true
	}
}
