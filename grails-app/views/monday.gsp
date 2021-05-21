<%--
  Created by IntelliJ IDEA.
  User: torr
  Date: 5/20/2021
  Time: 9:31 AM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main" />
        <title>Mondays</title>
    </head>
    <body>
        <div style="display:table-row">
            <div style="display:table-cell">
                <button id="nextButton">Next</button>
            </div>
            <div id="nextName" style="display:table-cell"></div>
        </div>
        <g:render template="/templates/may2421"/>
        <script>
            $(function() {
                var names = ['Anyone', 'Craig', 'TimN', 'Magali', 'TommyLee', 'Timo'];//, 'Mary'
                $('#nextButton').click(function() {
                    document.getElementById("nextName").innerHTML = getName();
                });

                function getName() {
                    var testMap = new Map();
                    for (let i = 0; i < 1000; i++) {
                        const name = Math.floor(Math.random() * names.length);
                        if (testMap.has(name)) {
                            let values = testMap.get(name);
                            values += 1;
                            testMap.set(name, values);
                        } else {
                            let value = 1;
                            testMap.set(name, value);
                        }
                    }

                    let displayValue = 0;
                    let html = "";
                    for (const x of testMap.keys()) {
                        let value = testMap.get(x);
                        if (value > displayValue) {
                            displayValue = value;
                            html = names[x];
                        } else if (value === displayValue) {
                            html += names[x];
                        }
                    }

                    return html;
                }

                // console.log(getName());
            });
        </script>
    </body>
</html>