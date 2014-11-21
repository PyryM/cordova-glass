var exec = require("cordova/exec");

/**
 * Constructor.
 *
 * @returns {Glass}
 */
function Glass() { }

/**
 * Returns the voice prompt params from the application launch
 *
 *
 *  com.rossgerbasi.cordova.glass.core.getLaunchParams(
 *      function(results) {
 *          console.log("Got those launch Params");
 *          console.log(results);
 *      },
 *      function () {
 *          console.log("Error getting those launch Params");
 *      }
 *  );
 *
 * @param successCallback first parameter returned is an Array of prompt results
 * @param errorCallback
 */
Glass.prototype.getLaunchParams = function (successCallback, errorCallback) {
    if (errorCallback == null) {
        errorCallback = function () {
        };
    }

    if (typeof errorCallback != "function") {
        console.log("Glass.getLaunchParams failure: failure parameter not a function");
        return;
    }

    if (typeof successCallback != "function") {
        console.log("Glass.getLaunchParams failure: success callback parameter must be a function");
        return;
    }

    exec(successCallback, errorCallback, 'Glass', 'get_launch_params', []);
};

/**
 * Invokes the speech recognition activity and returns the result
 *
 *
 *  com.rossgerbasi.cordova.glass.core.doSpeechRecognition(
 *      function(text) {
 *          console.log("Got recognized text: " + text);
 *      },
 *      function () {
 *          console.log("Error getting recognized text.");
 *      }
 *  );
 *
 * @param successCallback only returned parameter is recognized speech as a string
 * @param errorCallback
 */
Glass.prototype.doSpeechRecognition = function (successCallback, errorCallback) {
    if (errorCallback == null) {
        errorCallback = function () {
        };
    }

    if (typeof errorCallback != "function") {
        console.log("Glass.getLaunchParams failure: failure parameter not a function");
        return;
    }

    if (typeof successCallback != "function") {
        console.log("Glass.getLaunchParams failure: success callback parameter must be a function");
        return;
    }

    exec(successCallback, errorCallback, 'Glass', 'do_speech_recognition', []);
}

module.exports = new Glass();
