package cherokee

//import grails.transaction.Transactional

import static org.springframework.http.HttpStatus.*

//@Transactional(readOnly = false)
class SettingsController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Settings.list(params), model:[settingsInstanceCount: Settings.count()]
    }

    def show(Settings settingsInstance) {
        respond settingsInstance
    }

    def create() {
        respond new Settings(params)
    }

//    @Transactional
    def save(Settings settingsInstance) {
        if (settingsInstance == null) {
            notFound()
            return
        }

        if (settingsInstance.hasErrors()) {
            respond settingsInstance.errors, view:'create'
            return
        }

        settingsInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'settings.label', default: 'Settings'), settingsInstance.id])
                redirect settingsInstance
            }
            '*' { respond settingsInstance, [status: CREATED] }
        }
    }

    def edit(Settings settingsInstance) {
        respond settingsInstance
    }

//    @Transactional
    def update(Settings settingsInstance) {
        if (settingsInstance == null) {
            notFound()
            return
        }

        if (settingsInstance.hasErrors()) {
            respond settingsInstance.errors, view:'edit'
            return
        }

        settingsInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Settings.label', default: 'Settings'), settingsInstance.id])
                redirect settingsInstance
            }
            '*'{ respond settingsInstance, [status: OK] }
        }
    }

//    @Transactional
    def delete(Settings settingsInstance) {

        if (settingsInstance == null) {
            notFound()
            return
        }

        settingsInstance.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Settings.label', default: 'Settings'), settingsInstance.id])
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
