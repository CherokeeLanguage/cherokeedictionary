package net.cherokeedictionary.admin

class Announcements {

    //https://stackoverflow.com/questions/10957238/incorrect-string-value-when-trying-to-insert-utf-8-into-mysql-via-jdbc/10959780
    //ALTER TABLE announcements MODIFY COLUMN announcement VARCHAR(255)       CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL;
    //https://learn.yancyparedes.net/2012/02/ordering-list-columns-of-scaffold-page-in-grails/
    static constraints = {
        title blank: false, nullable: false
        announcement widget: 'textarea', blank: false, nullable: false
        title()
        announcement()
        enabled()
    }

    String title
    String announcement
    boolean enabled

    String toString() {
        return title
    }
}
