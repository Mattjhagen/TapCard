import CoreNFC
import Foundation

/// Physical NFC tag read/write - mirrors NfcService.kt on Android.
///
/// Important platform difference: Android's NfcService (and this class) only ever read/write a
/// *physical* NDEF tag (a sticker) - Android does not use Host Card Emulation either. iOS has no
/// public API for a third-party app to emulate an NFC tag, so a genuine "tap two phones together"
/// flow is not possible on iOS; this class provides full parity with what the Android app actually
/// does today (program a physical tag with the profile URL, and read one back).
///
/// NOTE: could not be compiled/tested locally (no Xcode/macOS toolchain available). CoreNFC also
/// requires a physical device - none of this can be exercised in the Simulator.
final class NFCTagService: NSObject {
    static let shared = NFCTagService()

    private var session: NFCNDEFReaderSession?
    private var pendingURLToWrite: String?
    private var onProgrammingResult: ((Bool, String) -> Void)?
    private var onReadResult: ((String?) -> Void)?

    var availability: NfcState {
        NFCNDEFReaderSession.readingAvailable ? .ready : .unavailable
    }

    /// Writes `url` to a physical NFC tag the user taps their phone against.
    func startProgramming(url: String, onResult: @escaping (Bool, String) -> Void) {
        guard availability == .ready else {
            onResult(false, "NFC is not available on this device.")
            return
        }
        pendingURLToWrite = url
        onProgrammingResult = onResult
        onReadResult = nil

        // invalidateAfterFirstRead: false so the session stays open long enough to connect + write.
        session = NFCNDEFReaderSession(delegate: self, queue: nil, invalidateAfterFirstRead: false)
        session?.alertMessage = "Hold your iPhone near an NFC tag to program it."
        session?.begin()
    }

    /// Reads a physical NFC tag and returns the embedded URL, if any.
    func startReading(onResult: @escaping (String?) -> Void) {
        guard availability == .ready else {
            onResult(nil)
            return
        }
        onReadResult = onResult
        onProgrammingResult = nil
        pendingURLToWrite = nil

        session = NFCNDEFReaderSession(delegate: self, queue: nil, invalidateAfterFirstRead: true)
        session?.alertMessage = "Hold your iPhone near an NFC tag to read it."
        session?.begin()
    }

    func stop() {
        session?.invalidate()
        session = nil
    }
}

extension NFCTagService: NFCNDEFReaderSessionDelegate {
    /// Simple auto-read path, used by `startReading`.
    func readerSession(_ session: NFCNDEFReaderSession, didDetectNDEFs messages: [NFCNDEFMessage]) {
        let url = messages
            .flatMap { $0.records }
            .compactMap { NDEFURIRecordParser.parse($0) }
            .first
        onReadResult?(url)
    }

    /// Manual tag-connect path, used by `startProgramming` so we can write to the tag.
    func readerSession(_ session: NFCNDEFReaderSession, didDetect tags: [NFCNDEFTag]) {
        guard let tag = tags.first, let urlToWrite = pendingURLToWrite, let onProgrammingResult else { return }

        session.connect(to: tag) { [weak self] error in
            if let error {
                onProgrammingResult(false, "Failed to connect to tag: \(error.localizedDescription)")
                session.invalidate()
                return
            }
            self?.writeURL(urlToWrite, to: tag, session: session, onResult: onProgrammingResult)
        }
    }

    func readerSession(_ session: NFCNDEFReaderSession, didInvalidateWithError error: Error) {
        self.session = nil
    }

    private func writeURL(_ url: String, to tag: NFCNDEFTag, session: NFCNDEFReaderSession, onResult: @escaping (Bool, String) -> Void) {
        tag.queryNDEFStatus { status, capacity, error in
            if let error {
                onResult(false, "Failed to read tag status: \(error.localizedDescription)")
                session.invalidate()
                return
            }

            switch status {
            case .notSupported:
                onResult(false, "This tag is not NDEF-compatible.")
                session.invalidate()
            case .readOnly:
                onResult(false, "NFC tag is read-only.")
                session.invalidate()
            case .readWrite:
                guard let nsURL = URL(string: url), let payload = NFCNDEFPayload.wellKnownTypeURIPayload(url: nsURL) else {
                    onResult(false, "Failed to build NDEF payload.")
                    session.invalidate()
                    return
                }
                let message = NFCNDEFMessage(records: [payload])
                if capacity < message.length {
                    onResult(false, "NFC tag capacity is too small.")
                    session.invalidate()
                    return
                }
                tag.writeNDEF(message) { error in
                    if let error {
                        onResult(false, "Failed to write to NFC tag: \(error.localizedDescription)")
                    } else {
                        onResult(true, "Tag programmed successfully!")
                    }
                    session.invalidate()
                }
            @unknown default:
                onResult(false, "Unknown tag status.")
                session.invalidate()
            }
        }
    }
}

/// Decodes an NFC Forum "URI Record Type Definition" well-known record back into a URL string.
/// The first payload byte is a prefix code (see NFC Forum RTD-URI 1.0 spec section 3.2.2);
/// the rest is the ASCII/UTF-8 suffix.
private enum NDEFURIRecordParser {
    private static let prefixes = [
        "", "http://www.", "https://www.", "http://", "https://", "tel:", "mailto:",
        "ftp://anonymous:anonymous@", "ftp://ftp.", "ftps://", "sftp://", "smb://", "nfs://",
        "ftp://", "dav://", "news:", "telnet://", "imap:", "rtsp://", "urn:", "pop:", "sip:",
        "sips:", "tftp:", "btspp://", "btl2cap://", "btgoep://", "tcpobex://", "irdaobex://",
        "file://", "urn:epc:id:", "urn:epc:tag:", "urn:epc:pat:", "urn:epc:raw:", "urn:epc:",
        "urn:nfc:"
    ]

    static func parse(_ record: NFCNDEFPayload) -> String? {
        guard record.typeNameFormat == .nfcWellKnown,
              record.type == Data("U".utf8),
              let prefixByte = record.payload.first else { return nil }

        let prefix = Int(prefixByte) < prefixes.count ? prefixes[Int(prefixByte)] : ""
        let suffixData = record.payload.dropFirst()
        guard let suffix = String(data: suffixData, encoding: .utf8) else { return nil }
        return prefix + suffix
    }
}
