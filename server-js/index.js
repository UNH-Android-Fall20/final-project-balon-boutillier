var admin = require("firebase-admin")
const express = require('express')
var serviceAccount = require("./private-key.json")

/*
 * Express server API endpoint for push notifcations
 *
 * @ref: https://www.techotopia.com/index.php/Sending_Firebase_Cloud_Messages_from_a_Node.js_Server
 */

 const apiPort = 5001

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://final-project-9c2ed.firebaseio.com"
});

// create the payload
var payload = {
    notification: {
      title: "Upload Successful",
      body: "Congratulations! You backed up a new file!"
    }
  };

// set the options to high, so they instantly get it
var options = {
    priority: "high",
    timeToLive: 60 * 60 *24
};

var registrationToken = "emJh4NBORn-doZcUcm9hGA:APA91bHJrdbkc7CuthbuzeZ85p_mwL4aIhR7h3K6J2WQ-hG7eKOU_lB6jtwaFpaiFfKzR0yILPGhON8FXAtelO3nAWzdvxJIw_uNbMpQtiG1tv3buGDqQSuhnTFIGnIOBydUbNi12YvZ";

const app = express()
app.get('/api/newBackupNotification', async(req, res) => {
  admin.messaging().sendToDevice(registrationToken, payload, options)
  .then(function(response) {
    console.log("Successfully sent message:", response);
  })
  .catch(function(error) {
    console.log("Error sending message:", error);
  });

  res.send("query complete")
});

/* start server */
app.listen(apiPort, () => console.log(`Server is live on ${apiPort}`));