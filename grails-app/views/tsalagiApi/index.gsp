<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    %{--
      Uncomment the Sitemesh layout line below if you want the trainer wrapped
      inside your Grails site's main header/footer layout instead of full-screen:
      <meta name="layout" content="main"/>
    --}%
    <title>${pageTitle ?: 'ᏣᎳᎩ Tsalagi — Cherokee Language & Curriculum Studio'}</title>

    <!-- Cherokee Syllabary + Display Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com" />
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
    <link
      href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=Noto+Sans+Cherokee:wght@400;600;700;800&family=Plus+Jakarta+Sans:wght@600;700;800&display=swap"
      rel="stylesheet"
    />

    <!-- Compiled Tsalagi Tailwind + React Stylesheet (from `npm run build:grails`) -->
    <link
      rel="stylesheet"
      href="${createLink(uri: '/tsalagiTrainer/assets/tsalagi-app.css')}"
    />

    <!-- Inject Grails Context-Aware API Base URL & Optional CSRF Token -->
    <script>
      window.__TSALAGI_CONFIG__ = {
        apiBaseUrl: "${createLink(uri: '/tsalagiTrainer/api')}",
        uploadsBaseUrl: "${createLink(uri: '/tsalagiTrainer/uploads')}",
        csrfToken: "${request.getAttribute('_csrf')?.token ?: ''}",
        csrfHeader: "${request.getAttribute('_csrf')?.headerName ?: 'X-CSRF-TOKEN'}"
      };
    </script>
</head>
<body class="bg-stone-50 text-stone-900 antialiased">
    <div id="root"></div>

    <!-- Compiled Tsalagi React Application Bundle (from `npm run build:grails`) -->
    <script
      type="module"
      src="${createLink(uri: '/tsalagiTrainer/assets/tsalagi-app.js')}"
    ></script>
</body>
</html>
