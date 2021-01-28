'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp({
    credential: admin.credential.applicationDefault(),
});

const runtimeOptions = {
    timeoutSeconds: 313,
}

/* [] */
exports.scheduledAuthenticatedUserScan = functions.pubsub.schedule('3 of month 07:00').onRun((context) => {

    

    return true;
});
