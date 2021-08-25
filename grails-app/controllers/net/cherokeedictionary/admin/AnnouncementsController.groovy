package net.cherokeedictionary.admin


import grails.validation.ValidationException

import static org.springframework.http.HttpStatus.*

class AnnouncementsController {

    AnnouncementsService announcementsService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        boolean loggedin = session.getAttribute("loggedin")
        if (!loggedin) {
            redirect url: "/"
        } else {
            params.max = Math.min(max ?: 10, 100)
            respond announcementsService.list(params), model: [announcementsCount: announcementsService.count()]
        }
    }

    def show(Long id) {
        boolean loggedin = session.getAttribute("loggedin")
        if (!loggedin) {
            redirect url: "/"
        } else {
            respond announcementsService.get(id)
        }
    }

    def create() {
        boolean loggedin = session.getAttribute("loggedin")
        if (!loggedin) {
            redirect url: "/"
        } else {
            respond new Announcements(params)
        }
    }

    def save(Announcements announcements) {
        boolean loggedin = session.getAttribute("loggedin")
        if (!loggedin) {
            redirect url: "/"
        } else {
            if (announcements == null) {
                notFound()
                return
            }

            try {
                announcementsService.save(announcements)
            } catch (ValidationException e) {
                respond announcements.errors, view: 'create'
                return
            }

            request.withFormat {
                form multipartForm {
                    flash.message = message(code: 'default.created.message', args: [message(code: 'announcements.label', default: 'Announcements'), announcements.id])
                    redirect announcements
                }
                '*' { respond announcements, [status: CREATED] }
            }
        }
    }

    def edit(Long id) {
        boolean loggedin = session.getAttribute("loggedin")
        if (!loggedin) {
            redirect url: "/"
        } else {
            respond announcementsService.get(id)
        }
    }

    def update(Announcements announcements) {
        boolean loggedin = session.getAttribute("loggedin")
        if (!loggedin) {
            redirect url: "/"
        } else {
            if (announcements == null) {
                notFound()
                return
            }

            try {
                announcementsService.save(announcements)
            } catch (ValidationException e) {
                respond announcements.errors, view: 'edit'
                return
            }

            request.withFormat {
                form multipartForm {
                    flash.message = message(code: 'default.updated.message', args: [message(code: 'announcements.label', default: 'Announcements'), announcements.id])
                    redirect announcements
                }
                '*' { respond announcements, [status: OK] }
            }
        }
    }

    def delete(Long id) {
        boolean loggedin = session.getAttribute("loggedin")
        if (!loggedin) {
            redirect url: "/"
        } else {
            if (id == null) {
                notFound()
                return
            }

            announcementsService.delete(id)

            request.withFormat {
                form multipartForm {
                    flash.message = message(code: 'default.deleted.message', args: [message(code: 'announcements.label', default: 'Announcements'), id])
                    redirect action: "index", method: "GET"
                }
                '*' { render status: NO_CONTENT }
            }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'announcements.label', default: 'Announcements'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
