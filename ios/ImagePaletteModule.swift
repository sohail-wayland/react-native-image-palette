//
//  ImagePalette.swift
//  react-native-image-palette
//
//  Created by Andrei Khavkunov on 01.09.2024.
//

import Foundation
import UIKit


@objc public class ImagePaletteModule: NSObject {
    
    enum ERRORS {
        static let INVALID_URL = "Invalid URL."
        static let DOWNLOAD_ERR = "Could not download image."
        static let PARSE_ERR = "Could not parse image."
        static let INVALID_FALLBACK_COLOR = "Invalid fallback hex color. Must be in the format #ffffff or #fff."
    }


    
    private func toHexString(color: UIColor) -> String {
        let comp = color.cgColor.components

        let r: CGFloat = comp![0]
        let g: CGFloat = comp![1]
        let b: CGFloat = comp![2]

        let rgb: Int = (Int)(r * 255) << 16 | (Int)(g * 255) << 8 | (Int)(b * 255) << 0

        return String(format: "#%06X", rgb)
    }
    
    
    private func parseFallbackColor(hexColor: String) -> String? {
        let regex = try! NSRegularExpression(pattern: "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", options: .caseInsensitive)

        let range = NSRange(location: 0, length: hexColor.utf16.count)
        let match = regex.firstMatch(in: hexColor, options: [], range: range)

        if match == nil {
            return nil
        }

        if hexColor.count == 7 {
            return hexColor
        }

        let red = String(hexColor[hexColor.index(hexColor.startIndex, offsetBy: 1)])
        let green = String(hexColor[hexColor.index(hexColor.startIndex, offsetBy: 2)])
        let blue = String(hexColor[hexColor.index(hexColor.startIndex, offsetBy: 3)])

        return "#\(red)\(red)\(green)\(green)\(blue)\(blue)"
    }
    
    
    @objc public func getAverageColor(
        uri: String,
        fallback: String,
        headers: [String: String]?,
        onResolve: @escaping (String) -> Void,
        onReject: @escaping (String, String, Error) -> Void
    ) {
        guard let fallbackColor = parseFallbackColor(hexColor: fallback) else {
            let error = NSError.init(domain: ImagePaletteModule.ERRORS.INVALID_FALLBACK_COLOR, code: -1)
            onReject("[ImagePalette]", error.localizedDescription, error)
            return
        }
        
        guard let parsedUri = URL(string: uri) else {
            let error = NSError.init(domain: ImagePaletteModule.ERRORS.INVALID_URL, code: -1)
            onReject("[ImagePalette]", error.localizedDescription, error)
            return
        }
        
        var request = URLRequest(url: parsedUri)

        headers?.forEach { key, value in
            request.setValue(value, forHTTPHeaderField: key)
        }
        
        URLSession.shared.dataTask(with: request) {(data, response, error) in
            guard let data = data, error == nil else {
                let error = NSError.init(domain: ImagePaletteModule.ERRORS.DOWNLOAD_ERR, code: -2)
                onReject("[ImagePalette]", error.localizedDescription, error)
                return
            }
            
            guard let uiImage = UIImage(data: data) else {
                let error = NSError.init(domain: ImagePaletteModule.ERRORS.PARSE_ERR, code: -3)
                onReject("[ImagePalette]", error.localizedDescription, error)
                return
            }
            
            let fallbackColor = UIColor(red: CGFloat(0), green: CGFloat(0), blue: CGFloat(0), alpha: 1)
            
            
            guard let inputImage = CIImage(image: uiImage) else {
                onResolve(self.toHexString(color: fallbackColor))
                return
            }
            
            let extentVector = CIVector(x: inputImage.extent.origin.x, y: inputImage.extent.origin.y, z: inputImage.extent.size.width, w: inputImage.extent.size.height)

            guard let filter = CIFilter(name: "CIAreaAverage", parameters: [kCIInputImageKey: inputImage, kCIInputExtentKey: extentVector]) else {
                onResolve(self.toHexString(color: fallbackColor))
                return
            }
            guard let outputImage = filter.outputImage else {
                onResolve(self.toHexString(color: fallbackColor))
                return
            }

            var bitmap = [UInt8](repeating: 0, count: 4)
            let context = CIContext(options: [.workingColorSpace: kCFNull!])
            context.render(outputImage, toBitmap: &bitmap, rowBytes: 4, bounds: CGRect(x: 0, y: 0, width: 1, height: 1), format: .RGBA8, colorSpace: nil)

            let avgColor = UIColor(
                red: CGFloat(bitmap[0]) / 255,
                green: CGFloat(bitmap[1]) / 255,
                blue: CGFloat(bitmap[2]) / 255,
                alpha: CGFloat(bitmap[3]) / 255
            )
            
            onResolve(self.toHexString(color: avgColor))
            
        }.resume()
    }
    
//    @objc public func getPalette(
//        uri: String,
//        fallback: String,
//        headers: [String: String]?,
//        quality: String = "low"
//    ) {
//        guard let fallbackColor = parseFallbackColor(hexColor: fallback) else {
//            let error = NSError.init(domain: ImagePaletteManager.ERRORS.INVALID_FALLBACK_COLOR, code: -1)
//            onReject(error)
//            return
//        }
//        
//        guard let parsedUri = URL(string: uri) else {
//            let error = NSError.init(domain: ImagePaletteManager.ERRORS.INVALID_URL, code: -1)
//            onReject(error)
//            return
//        }
//        
//        var request = URLRequest(url: parsedUri)
//
//        headers?.forEach { key, value in
//            request.setValue(value, forHTTPHeaderField: key)
//        }
//        
//        URLSession.shared.dataTask(with: request) {(data, response, error) in
//            guard let data = data, error == nil else {
//                self.onReject(NSError.init(domain: ImagePaletteManager.ERRORS.DOWNLOAD_ERR, code: -2))
//                return
//            }
//
//            guard let uiImage = UIImage(data: data) else {
//                let error = NSError.init(domain: ImagePaletteManager.ERRORS.PARSE_ERR, code: -3)
//                self.onReject(error)
//                return
//            }
//            
//            let qualityProp = quality
//            let quality = self.getQuality(qualityOption: qualityProp)
//            
//            
//            uiImage.getColors(quality: quality) { colors in
//                 var resultDict: Dictionary<String, String> = ["platform": "ios"]
//
//                 if let background = colors?.background {
//                     resultDict["background"] = self.toHexString(color: background)
//                 } else {
//                     resultDict["background"] = fallbackColor
//                 }
//
//
//                 if let primary = colors?.primary {
//                     resultDict["primary"] = self.toHexString(color: primary)
//                 } else {
//                     resultDict["primary"] = fallbackColor
//                 }
//
//
//                 if let secondary = colors?.secondary {
//                     resultDict["secondary"] = self.toHexString(color: secondary)
//                 } else {
//                     resultDict["secondary"] = fallbackColor
//                 }
//
//                 if let detail = colors?.detail {
//                     resultDict["detail"] = self.toHexString(color: detail)
//                 } else {
//                     resultDict["detail"] = fallbackColor
//                 }
//
//                self.onResolve(resultDict)
//             }
//
//        }.resume()
//    
//    }
    
}
