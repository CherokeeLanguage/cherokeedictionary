function appender(value) {
    var displayElement = document.getElementById('display');
    displayElement.append(value);
    displayElement.appendChild(document.createElement("br"));
}

function runTests() {
    appender(JSON.stringify(getBreakdown('ᎯᏬᏂᎯ')));
    // appender('ᎠᎡᎢᎣᎤᎥᎦ' == tsalagiToSyllabary('aeiouvga'));
    // appender('ᎠᎡᎢᎣᎤᎥᎦ' == tsalagiToSyllabary('aeiouvga'));
}

// uncomment here or in the html page
// runTests();