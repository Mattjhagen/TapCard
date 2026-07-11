import UIKit
import CoreImage.CIFilterBuiltins

enum QRCodeService {
    /// Generates a QR code image for the given content - mirrors QRCodeGenerator.kt on Android.
    static func generate(content: String, size: CGFloat = 1024) -> UIImage? {
        guard !content.isEmpty else { return nil }

        let context = CIContext()
        let filter = CIFilter.qrCodeGenerator()
        filter.message = Data(content.utf8)
        filter.correctionLevel = "M"

        guard let outputImage = filter.outputImage else { return nil }

        let scale = size / outputImage.extent.width
        let transformed = outputImage.transformed(by: CGAffineTransform(scaleX: scale, y: scale))

        guard let cgImage = context.createCGImage(transformed, from: transformed.extent) else { return nil }
        return UIImage(cgImage: cgImage)
    }

    /// Saves a QR code image to the Photos library - mirrors QRExportService.saveQrToGallery on Android.
    static func saveToPhotoLibrary(_ image: UIImage) {
        UIImageWriteToSavedPhotosAlbum(image, nil, nil, nil)
    }
}
