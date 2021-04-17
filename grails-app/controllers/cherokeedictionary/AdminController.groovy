package cherokee

//import grails.transaction.Transactional

import static org.springframework.http.HttpStatus.*

//@Transactional(readOnly = true)
class AdminController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        boolean loggedin = session.getAttribute("loggedin")
        if (!loggedin) {
            redirect url: "/"
        } else {
            params.max = Math.min(max ?: 10, 100)
            respond Admin.list(params), model: [adminInstanceCount: Admin.count()]
        }
    }

    def logout() {
        session.removeAttribute("loggedin")
        redirect url: "/"
    }

    def show(Admin adminInstance) {
        respond adminInstance
    }

    def create() {
        respond new Admin(params)
    }

//    @Transactional
    def save(Admin adminInstance) {
        if (adminInstance == null) {
            notFound()
            return
        }

        if (adminInstance.hasErrors()) {
            respond adminInstance.errors, view:'create'
            return
        }

        adminInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'admin.label', default: 'Admin'), adminInstance.id])
                redirect adminInstance
            }
            '*' { respond adminInstance, [status: CREATED] }
        }
    }

    def edit(Admin adminInstance) {
        respond adminInstance
    }

//    @Transactional
    def update(Admin adminInstance) {
        if (adminInstance == null) {
            notFound()
            return
        }

        if (adminInstance.hasErrors()) {
            respond adminInstance.errors, view:'edit'
            return
        }

        adminInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Admin.label', default: 'Admin'), adminInstance.id])
                redirect adminInstance
            }
            '*'{ respond adminInstance, [status: OK] }
        }
    }

//    @Transactional
    def delete(Admin adminInstance) {

        if (adminInstance == null) {
            notFound()
            return
        }

        adminInstance.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Admin.label', default: 'Admin'), adminInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'admin.label', default: 'Admin'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

    def login() {
        def username = params.username;
        def password = params.password;

        List lst = Admin.findAll("from Admin where username=?0 and password=?1", [username, password])

        boolean matched = false;
        if (lst) {
            matched = true;
//            lst.each {
//                println it
//            }
        }

        if (matched) {
            session.setAttribute("loggedin", true)
            session.setAttribute("admin", lst.get(0))
        } else {
            session.removeAttribute("loggedin")
        }

        redirect(uri: "/")
    }
}
