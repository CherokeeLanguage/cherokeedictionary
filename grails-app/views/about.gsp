<%--
/**
 *
 * @author torr
 * @since 5/31/14 10:09 AM
 */
--%>

<%@ page import="net.cherokeedictionary.admin.SourceManagement" contentType="text/html;charset=UTF-8" %>
<html>
  <head>
      <meta name="layout" content="manager"/>
      <title>About</title>
  </head>
  <a>
  <b>About</b><br/>
  <br/>
  The purpose of this online searchable dictionary database is to make access to the contents of rigorously reviewed language materials more widely available and more easily searchable in a combined database that maintains the integrity of the sources upon which it draws. The contents of this page are derived from several sources:

        <ul>
            <g:each var="source" in="${net.cherokeedictionary.admin.SourceManagement.findAll()}">
              <li>${source.bibliographyFullAPA}</li>
            </g:each>
        </ul>

  All of these sources are vetted by reputable, fluent Cherokee language speakers and the above sources are cited for every entry in the combined database. This is to maintain transparency for the user so they can judge the authenticity of these definitions, as well as to attribute credit where due. Though word definitions are not protected by copyright, original content like the example sentences in the CED are protected and are used here with permission. Microsoft terms are used under license.
  There are currently many Cherokee-English word lists available on the internet, but most of these do not provide basic dictionary information like part of speech, different conjugations or inflections, or example sentences. Dr. Feeling's dictionary, however, provides all of these and is the definitive Cherokee language resource. For this reason, the CED is foundation of this database.
  The current online project was accomplished by a team of Cherokee language students who volunteered to retype the CED entries one-by-one into an Excel spreadsheet and develop the database and web platform*. The completed entries were then uploaded into a database and published on this website with capacity to search in phonetic Cherokee, Cherokee syllabary, and English; Cherokee searches will catch many spelling variants, which is a huge leap forward. After the basic database structure was established and populated with entries from the CED, the other sources were added. As new sources become available, those will be added as well. Only well documented language resources will be added to the database, so learners will know the information is valid and accurate.
  This project is made freely available to all in the spirit of openness and gratitude to the ancestors and the many people who made the original work possible.
  It is our hope that this online dictionary database will expand and contribute in perpetuity to the preservation and growth of the Cherokee language.
  <br/>
  Wado/Sgi!<br/>
  CED-Online Project Development Team<br/>
  Tim Nuttle, Tim Orr, TommyLee Whitlock, and Sarah Orndorff<br/>
  <br/>
  <br/>
  Regarding the Microsoft Agreement on the site: According to the terms of using the Microsoft terminology we are posting their agreement to not hold them liable for anything, basically. And in doing so we've stated that by displaying their agreement <a href="https://www.microsoft.com/en-us/language/LicenseAgreement">https://www.microsoft.com/en-us/language/LicenseAgreement</a>. As well, they don't support our endeavor in any way. We're using their terms in our database and that's it.
  <br/>
  <br/>
  * Tim Nuttle initiated and organized the project. Tim Orr designed the database and web layout. TommyLee Whitlock and Sarah Orndorff spearheaded data entry and quality assurance, supported by Lee Lewis, Buffy Turner, Britton Watkins, Roberta Sue Allen, Aiden Archer Rain Warrior-Milford, Leesia Marshall, Tina Phronjia Bradley, Crystal Hofegartner, Mary Rae, Elaine Tuttle, Barb Miller, Laverne Swinton, Honor Keeler, and Angela Arp, Tim Orr, and Tim Nuttle. Michael Joyner performed additional quality assurance.
  <br/>
  Comments or suggestions? Send us an email: <b>cherokeedictionarynet AT gmail DOT com</b>
  <br/>

  </body>
</html>