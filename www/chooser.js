function getFileInternal(accept, includeData, pickFolders, allowMultiple, successCallback, failureCallback) {
    var args = [accept, includeData, pickFolders, allowMultiple];

    console.log('Calling Cordova Plugin with args:', args);  // Log arguments
    var result = new Promise(function (resolve, reject) {
        cordova.exec(resolve, reject, 'Chooser', 'getDirectory', args);
    });

    result.then(successCallback).catch(failureCallback);

    return result;
}

module.exports = {
    getFile: function (accept, successCallback, failureCallback) {
        return getFileInternal(accept, true, false, false, successCallback, failureCallback);
    },
    getFileMetadata: function (accept, successCallback, failureCallback) {
        return getFileInternal(accept, false, false, false, successCallback, failureCallback);
    },
    getFolder: function (accept, allowMultiple, successCallback, failureCallback) {
        return getFileInternal(accept, false, true, allowMultiple, successCallback, failureCallback);
    }
};
