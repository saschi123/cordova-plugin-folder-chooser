package com.folder.cordova;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;


public class Chooser extends CordovaPlugin {
    private static final String ACTION_OPEN_DIRECTORY = "getDirectory";
    private static final int PICK_DIRECTORY_REQUEST = 1;

    private CallbackContext callbackContext;

    public void chooseDirectory(CallbackContext callbackContext) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        cordova.startActivityForResult(this, intent, PICK_DIRECTORY_REQUEST);

        PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
        pluginResult.setKeepCallback(true);
        this.callbackContext = callbackContext;
        callbackContext.sendPluginResult(pluginResult);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        if (ACTION_OPEN_DIRECTORY.equals(action)) {
            this.chooseDirectory(callbackContext);
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_DIRECTORY_REQUEST && this.callbackContext != null) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Uri uri = data.getData();

                if (uri != null) {
                    // Persist access permissions.
                    cordova.getActivity().getContentResolver().takePersistableUriPermission(uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    callbackContext.success(uri.toString());
                } else {
                    callbackContext.error("Directory URI was null.");
                }
            } else {
                callbackContext.error("Directory selection was cancelled or failed.");
            }
        }
    }
}
