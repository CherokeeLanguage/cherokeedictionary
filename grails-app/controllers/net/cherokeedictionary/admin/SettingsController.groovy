package net.cherokeedictionary.admin


import grails.validation.ValidationException

import static org.springframework.http.HttpStatus.*

class SettingsController {

    SettingsService settingsService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        boolean loggedin = session.getAttribute("loggedin")
        if (!loggedin) {
            redirect url: "/"
        } else {
            params.max = Math.min(max ?: 10, 100)
            respond settingsService.list(params), model:[settingsCount: settingsService.count()]
        }
    }

    def show(Long id) {
        respond settingsService.get(id)
    }

    def create() {
        respond new Settings(params)
    }

    def save(Settings settings) {
        if (settings == null) {
            notFound()
            return
        }

        try {
            settingsService.save(settings)
        } catch (ValidationException e) {
            respond settings.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'settings.label', default: 'Settings'), settings.id])
                redirect settings
            }
            '*' { respond settings, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond settingsService.get(id)
    }

    def update(Settings settings) {
        if (settings == null) {
            notFound()
            return
        }

        try {
            settingsService.save(settings)
        } catch (ValidationException e) {
            respond settings.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'settings.label', default: 'Settings'), settings.id])
                redirect settings
            }
            '*'{ respond settings, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        settingsService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'settings.label', default: 'Settings'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'settings.label', default: 'Settings'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
