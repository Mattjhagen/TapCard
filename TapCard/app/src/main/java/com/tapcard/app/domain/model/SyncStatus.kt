package com.tapcard.app.domain.model

enum class SyncStatus {
    SAVED_LOCALLY,
    UPLOADING,
    SYNCING,
    SYNCED,
    SYNC_FAILED,
    SIGN_IN_TO_SYNC
}
