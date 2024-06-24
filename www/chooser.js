// Converts a base64 string to a Uint8Array
function from_base64(sBase64, nBlocksSize) {
    function _b64ToUint6(nChr) {
        return nChr > 64 && nChr < 91 ? nChr - 65 :
               nChr > 96 && nChr < 123 ? nChr - 71 :
               nChr > 47 && nChr < 58 ? nChr + 4 :
               nChr === 43 ? 62 :
               nChr === 47 ? 63 :
               0;
    }

    var nInLen = sBase64.length;
    var nOutLen = nBlocksSize ?
                  Math.ceil(((nInLen * 3 + 1) >> 2) / nBlocksSize) * nBlocksSize :
                  (nInLen * 3 + 1) >> 2;
    var taBytes = new Uint8Array(nOutLen);

    for (var nMod3, nMod4, nUint24 = 0, nOutIdx = 0, nInIdx = 0; nInIdx < nInLen; nInIdx++) {
        nMod4 = nInIdx & 3;
        nUint24 |= _b64ToUint6(sBase64.charCodeAt(nInIdx)) << (18 - 6 * nMod4);
        if (nMod4 === 3 || nInLen - nInIdx === 1) {
            for (nMod3 = 0; nMod3 < 3 && nOutIdx < nOutLen; nMod3++, nOutIdx++) {
                taBytes[nOutIdx] = (nUint24 >>> ((16 >>> nMod3) & 24)) & 255;
            }
            nUint24 = 0;
        }
    }

    return taBytes;
}

// Handles file/folder selection
function getFileInternal(accept, includeData, pickFolders, allowMultiple, successCallback, failureCallback) {
    var args = [accept, includeData, pickFolders, allowMultiple];

    cordova.exec(successCallback, failureCallback, 'Chooser', 'getFile', args);
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
