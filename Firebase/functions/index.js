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

    return admin.auth()
        .listUsers(1000)
        .then((listUsersResult) => {

            listUsersResult.users.forEach((userRecord) => {

                var userJsonData = userRecord.toJSON();

                if (userJsonData.emailVerified) {
                    console.log(userJsonData.email, 'Is Verified: ')

                } else {
                    console.log(userJsonData.email, 'Is Not Verified: ')

                    admin
                        .auth()
                        .deleteUser(userJsonData.uid)


                }

            });

            if (listUsersResult.pageToken) { listAllUsers(listUsersResult.pageToken); }

        })
        .catch((error) => {
            console.log('Error Listing Users: ', error);
        });;
});
