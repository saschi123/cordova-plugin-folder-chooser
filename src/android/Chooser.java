package com.folder.cordova;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.io.UnsupportedEncodingException;

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

                    String filePath = getPathFromUri(cordova.getActivity(), uri);
                    if (filePath != null) {
                        callbackContext.success("file://" + filePath);
                    } else {
                        callbackContext.error("Failed to convert URI to file path.");
                    }
                } else {
                    callbackContext.error("Directory URI was null.");
                }
            } else {
                callbackContext.error("Directory selection was cancelled or failed.");
            }
        }
    }

    private static String getPathFromUri(final Context context, final Uri uri) {
        if (DocumentsContract.isDocumentUri(context, uri)) {
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            final String type = split[0];

            if ("primary".equalsIgnoreCase(type)) {
                try {
                    String path = URLDecoder.decode(split[1], StandardCharsets.UTF_8.name());
                    return Environment.getExternalStorageDirectory() + "/" + path;
                } catch (UnsupportedEncodingException e) {
                    // This should never happen with UTF-8 encoding.
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
