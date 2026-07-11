import SwiftUI

struct BusinessCardPreview: View {
    let profile: Profile
    var profilePhotoImage: UIImage?
    var companyLogoImage: UIImage?

    private var textColor: Color { profile.isDarkTheme ? .white : Color(red: 0.18, green: 0.24, blue: 0.29) }
    private var secondaryTextColor: Color { textColor.opacity(0.7) }
    private var gradient: LinearGradient {
        let colors: [Color] = profile.isDarkTheme
            ? [Color(red: 0.12, green: 0.12, blue: 0.12), Color(red: 0.07, green: 0.07, blue: 0.07)]
            : [Color(red: 0.91, green: 0.96, blue: 0.91), Color(red: 0.78, green: 0.90, blue: 0.79)]
        return LinearGradient(colors: colors, startPoint: .topLeading, endPoint: .bottomTrailing)
    }

    var body: some View {
        ZStack {
            RoundedRectangle(cornerRadius: 24, style: .continuous)
                .fill(gradient)
                .shadow(radius: 12)

            VStack(alignment: .leading, spacing: 0) {
                HStack(alignment: .top) {
                    HStack(spacing: 16) {
                        photoView
                        VStack(alignment: .leading, spacing: 2) {
                            Text(profile.fullName.isEmpty ? "Your Name" : profile.fullName)
                                .font(.system(size: 22, weight: .heavy))
                                .foregroundStyle(textColor)
                            Text(profile.jobTitle.isEmpty ? "Job Title" : profile.jobTitle)
                                .font(.system(size: 15, weight: .semibold))
                                .foregroundStyle(secondaryTextColor)
                            Text(profile.company.isEmpty ? "Company" : profile.company)
                                .font(.system(size: 15, weight: .medium))
                                .foregroundStyle(secondaryTextColor)
                        }
                    }
                    Spacer()
                    if let companyLogoImage {
                        Image(uiImage: companyLogoImage)
                            .resizable()
                            .aspectRatio(contentMode: .fill)
                            .frame(width: 48, height: 48)
                            .clipShape(RoundedRectangle(cornerRadius: 8))
                    }
                }

                Spacer()

                VStack(alignment: .leading, spacing: 4) {
                    if !profile.phone.isEmpty { Text(profile.phone).foregroundStyle(textColor) }
                    if !profile.email.isEmpty { Text(profile.email).foregroundStyle(textColor) }
                    if !profile.website.isEmpty { Text(profile.website).foregroundStyle(textColor) }
                }
                .font(.system(size: 14))
            }
            .padding(24)
        }
        .aspectRatio(1.586, contentMode: .fit)
    }

    @ViewBuilder
    private var photoView: some View {
        if let profilePhotoImage {
            Image(uiImage: profilePhotoImage)
                .resizable()
                .aspectRatio(contentMode: .fill)
                .frame(width: 64, height: 64)
                .clipShape(Circle())
        } else {
            Circle()
                .fill(Color.gray.opacity(0.3))
                .frame(width: 64, height: 64)
                .overlay(
                    Text(profile.fullName.first.map(String.init)?.uppercased() ?? "?")
                        .font(.title2)
                        .foregroundStyle(textColor)
                )
        }
    }
}
