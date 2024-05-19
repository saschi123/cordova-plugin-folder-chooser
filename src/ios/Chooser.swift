import UIKit
import MobileCoreServices
import Foundation

class ChooserUIDocumentPickerViewController: UIDocumentPickerViewController {
    var includeData: Bool = false
    var allowMultipleSelection: Bool = false
}

@objc(Chooser)
class Chooser: CDVPlugin {
    var commandCallback: String?

    func callPicker(includeData: Bool, utis: [String], allowMultipleSelection: Bool, pickFolders: Bool) {
        let pickerMode: UIDocumentPickerMode = pickFolders ? .open : .import
        let picker = ChooserUIDocumentPickerViewController(documentTypes: utis, in: pickerMode)
        picker.delegate = self
        picker.includeData = includeData
        picker.allowsMultipleSelection = allowMultipleSelection
        viewController.present(picker, animated: true, completion: nil)
    }

    @objc(getFile:)
    func getFile(command: CDVInvokedUrlCommand) {
        self.commandCallback = command.callbackId
        let accept = command.arguments[0] as! String
        let includeData = command.arguments[1] as! Bool
        let pickFolders = command.arguments[2] as! Bool
        let allowMultipleSelection = command.arguments[3] as! Bool

        let utis = accept.components(separatedBy: ",").map { mimeType -> String in
            mimeType.toUTI()
        }
        callPicker(includeData: includeData, utis: utis, allowMultipleSelection: allowMultipleSelection, pickFolders: pickFolders)
    }

    func documentPicker(_ controller: UIDocumentPickerViewController, didPickDocumentsAt urls: [URL]) {
        guard let picker = controller as? ChooserUIDocumentPickerViewController else { return }
        for url in urls {
            documentWasSelected(includeData: picker.includeData, url: url)
        }
    }

    func documentPickerWasCancelled(_ controller: UIDocumentPickerViewController) {
        send("RESULT_CANCELED")
    }

    func documentWasSelected(includeData: Bool, url: URL) {
        // Handling security for file access in iOS
        let shouldStopAccessing = url.startAccessingSecurityScopedResource()
        defer {
            if shouldStopAccessing {
                url.stopAccessingSecurityScopedResource()
            }
        }

        // Continue with your existing implementation
    }
}

private extension String {
    func toUTI() -> String {
        // Existing UTI conversion logic here
    }
}
