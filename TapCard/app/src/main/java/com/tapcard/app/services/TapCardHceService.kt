package com.tapcard.app.services

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log
import com.tapcard.app.domain.repository.ProfileRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel
import javax.inject.Inject

@AndroidEntryPoint
class TapCardHceService : HostApduService() {

    @Inject
    lateinit var profileRepository: ProfileRepository

    private val selectApplication = byteArrayOf(
        0x00.toByte(), 0xA4.toByte(), 0x04.toByte(), 0x00.toByte(), 0x07.toByte(),
        0xD2.toByte(), 0x76.toByte(), 0x00.toByte(), 0x00.toByte(), 0x85.toByte(),
        0x01.toByte(), 0x01.toByte(), 0x00.toByte()
    )

    private val selectCcFile = byteArrayOf(
        0x00.toByte(), 0xA4.toByte(), 0x00.toByte(), 0x0C.toByte(), 0x02.toByte(),
        0xE1.toByte(), 0x03.toByte()
    )

    private val selectNdefFile = byteArrayOf(
        0x00.toByte(), 0xA4.toByte(), 0x00.toByte(), 0x0C.toByte(), 0x02.toByte(),
        0xE1.toByte(), 0x04.toByte()
    )

    private val ccFileContents = byteArrayOf(
        0x00.toByte(), 0x0F.toByte(), // CC length
        0x20.toByte(), // Mapping Version 2.0
        0x00.toByte(), 0x7F.toByte(), // Max R-APDU size
        0x00.toByte(), 0x7F.toByte(), // Max C-APDU size
        0x04.toByte(), // Tag: NDEF File Control TLV
        0x06.toByte(), // Length
        0xE1.toByte(), 0x04.toByte(), // NDEF File ID
        0x04.toByte(), 0x00.toByte(), // Max NDEF size (1024 bytes)
        0x00.toByte(), // Read access: free
        0xFF.toByte()  // Write access: write protected
    )

    private val ndefStatusOk = byteArrayOf(0x90.toByte(), 0x00.toByte())
    private val ndefStatusError = byteArrayOf(0x6A.toByte(), 0x82.toByte())

    private var selectedFile: FileType = FileType.NONE
    private var cachedUrl: String = "https://tapcard.space"
    private val serviceScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main + kotlinx.coroutines.SupervisorJob())

    private enum class FileType {
        NONE, CC, NDEF
    }

    override fun onCreate() {
        super.onCreate()
        serviceScope.launch {
            profileRepository.getProfileFlow().collect { profile ->
                if (profile != null) {
                    val username = profile.username.ifBlank { profile.id }
                    val slug = profile.profileSlug.ifBlank { "personal" }
                    cachedUrl = if (slug == "personal" || slug == "default") {
                        "https://tapcard.space/u/$username"
                    } else {
                        "https://tapcard.space/u/$username/$slug"
                    }
                    Log.d("TapCardHce", "Cached active URL: $cachedUrl")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun processCommandApdu(commandApdu: ByteArray?, extras: Bundle?): ByteArray {
        if (commandApdu == null) {
            return ndefStatusError
        }

        Log.d("TapCardHce", "APDU Command: ${commandApdu.toHex()}")

        if (commandApdu.contentEquals(selectApplication)) {
            selectedFile = FileType.NONE
            return ndefStatusOk
        }

        if (commandApdu.contentEquals(selectCcFile)) {
            selectedFile = FileType.CC
            return ndefStatusOk
        }

        if (commandApdu.contentEquals(selectNdefFile)) {
            selectedFile = FileType.NDEF
            return ndefStatusOk
        }

        // Handle Read Command (starts with 00 B0)
        if (commandApdu.size >= 5 && commandApdu[0] == 0x00.toByte() && commandApdu[1] == 0xB0.toByte()) {
            val offset = ((commandApdu[2].toInt() and 0xFF) shl 8) or (commandApdu[3].toInt() and 0xFF)
            var length = commandApdu[4].toInt() and 0xFF
            
            if (length == 0) length = 256

            return when (selectedFile) {
                FileType.CC -> {
                    if (offset >= ccFileContents.size) {
                        ndefStatusError
                    } else {
                        val end = (offset + length).coerceAtMost(ccFileContents.size)
                        val data = ccFileContents.sliceArray(offset until end)
                        data + ndefStatusOk
                    }
                }
                FileType.NDEF -> {
                    val ndefRecord = NdefRecord.createUri(cachedUrl)
                    val ndefMessage = NdefMessage(arrayOf(ndefRecord))
                    val ndefMessageBytes = ndefMessage.toByteArray()
                    
                    val ndefFileBytes = ByteArray(2 + ndefMessageBytes.size)
                    ndefFileBytes[0] = ((ndefMessageBytes.size shr 8) and 0xFF).toByte()
                    ndefFileBytes[1] = (ndefMessageBytes.size and 0xFF).toByte()
                    System.arraycopy(ndefMessageBytes, 0, ndefFileBytes, 2, ndefMessageBytes.size)

                    if (offset >= ndefFileBytes.size) {
                        ndefStatusError
                    } else {
                        val end = (offset + length).coerceAtMost(ndefFileBytes.size)
                        val data = ndefFileBytes.sliceArray(offset until end)
                        data + ndefStatusOk
                    }
                }
                else -> ndefStatusError
            }
        }

        return ndefStatusError
    }

    override fun onDeactivated(reason: Int) {
        selectedFile = FileType.NONE
        Log.d("TapCardHce", "Deactivated: reason = $reason")
    }

    private fun ByteArray.toHex(): String = joinToString("") { "%02X".format(it) }
}
