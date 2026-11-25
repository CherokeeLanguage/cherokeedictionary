<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    %{--
      Uncomment the Sitemesh layout line below to wrap the workbench in your
      Grails site's main header/footer layout instead of rendering full-screen:
      <meta name="layout" content="main"/>
    --}%
    <title>${pageTitle ?: 'Tsalagi Corpus Verification Workbench'}</title>

    <!-- Compiled React + Tailwind stylesheet (from `npm run build`) -->
    <link
      rel="stylesheet"
      href="${createLink(uri: '/word_verify/assets/tsalagi-app.css')}"
    />
</head>
<body class="bg-stone-50 text-stone-900 antialiased">
    <div id="root"></div>

    <!-- Compiled React application bundle (from `npm run build`) -->
    <script
      type="module"
      src="${createLink(uri: '/word_verify/assets/tsalagi-app.js')}"
    ></script>
</body>
</html>
