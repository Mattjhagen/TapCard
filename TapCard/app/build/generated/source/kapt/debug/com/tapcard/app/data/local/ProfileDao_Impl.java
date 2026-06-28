package com.tapcard.app.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@SuppressWarnings({"unchecked", "deprecation"})
public final class ProfileDao_Impl implements ProfileDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ProfileEntity> __insertionAdapterOfProfileEntity;

  public ProfileDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfProfileEntity = new EntityInsertionAdapter<ProfileEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `profile` (`id`,`fullName`,`jobTitle`,`company`,`phone`,`email`,`website`,`username`,`themeColorHex`,`isDarkTheme`,`isPublic`,`isPendingSync`,`profilePhotoLocalUri`,`companyLogoLocalUri`,`profilePhotoUrl`,`companyLogoUrl`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ProfileEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getId());
        }
        if (entity.getFullName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getFullName());
        }
        if (entity.getJobTitle() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getJobTitle());
        }
        if (entity.getCompany() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getCompany());
        }
        if (entity.getPhone() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getPhone());
        }
        if (entity.getEmail() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getEmail());
        }
        if (entity.getWebsite() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getWebsite());
        }
        if (entity.getUsername() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getUsername());
        }
        if (entity.getThemeColorHex() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getThemeColorHex());
        }
        final int _tmp = entity.isDarkTheme() ? 1 : 0;
        statement.bindLong(10, _tmp);
        final int _tmp_1 = entity.isPublic() ? 1 : 0;
        statement.bindLong(11, _tmp_1);
        final int _tmp_2 = entity.isPendingSync() ? 1 : 0;
        statement.bindLong(12, _tmp_2);
        if (entity.getProfilePhotoLocalUri() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getProfilePhotoLocalUri());
        }
        if (entity.getCompanyLogoLocalUri() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getCompanyLogoLocalUri());
        }
        if (entity.getProfilePhotoUrl() == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.getProfilePhotoUrl());
        }
        if (entity.getCompanyLogoUrl() == null) {
          statement.bindNull(16);
        } else {
          statement.bindString(16, entity.getCompanyLogoUrl());
        }
      }
    };
  }

  @Override
  public Object saveProfile(final ProfileEntity profile,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfProfileEntity.insert(profile);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<ProfileEntity> getProfileFlow() {
    final String _sql = "SELECT * FROM profile WHERE id = 'local_profile'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"profile"}, new Callable<ProfileEntity>() {
      @Override
      @Nullable
      public ProfileEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFullName = CursorUtil.getColumnIndexOrThrow(_cursor, "fullName");
          final int _cursorIndexOfJobTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "jobTitle");
          final int _cursorIndexOfCompany = CursorUtil.getColumnIndexOrThrow(_cursor, "company");
          final int _cursorIndexOfPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "phone");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfWebsite = CursorUtil.getColumnIndexOrThrow(_cursor, "website");
          final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
          final int _cursorIndexOfThemeColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "themeColorHex");
          final int _cursorIndexOfIsDarkTheme = CursorUtil.getColumnIndexOrThrow(_cursor, "isDarkTheme");
          final int _cursorIndexOfIsPublic = CursorUtil.getColumnIndexOrThrow(_cursor, "isPublic");
          final int _cursorIndexOfIsPendingSync = CursorUtil.getColumnIndexOrThrow(_cursor, "isPendingSync");
          final int _cursorIndexOfProfilePhotoLocalUri = CursorUtil.getColumnIndexOrThrow(_cursor, "profilePhotoLocalUri");
          final int _cursorIndexOfCompanyLogoLocalUri = CursorUtil.getColumnIndexOrThrow(_cursor, "companyLogoLocalUri");
          final int _cursorIndexOfProfilePhotoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "profilePhotoUrl");
          final int _cursorIndexOfCompanyLogoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "companyLogoUrl");
          final ProfileEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpFullName;
            if (_cursor.isNull(_cursorIndexOfFullName)) {
              _tmpFullName = null;
            } else {
              _tmpFullName = _cursor.getString(_cursorIndexOfFullName);
            }
            final String _tmpJobTitle;
            if (_cursor.isNull(_cursorIndexOfJobTitle)) {
              _tmpJobTitle = null;
            } else {
              _tmpJobTitle = _cursor.getString(_cursorIndexOfJobTitle);
            }
            final String _tmpCompany;
            if (_cursor.isNull(_cursorIndexOfCompany)) {
              _tmpCompany = null;
            } else {
              _tmpCompany = _cursor.getString(_cursorIndexOfCompany);
            }
            final String _tmpPhone;
            if (_cursor.isNull(_cursorIndexOfPhone)) {
              _tmpPhone = null;
            } else {
              _tmpPhone = _cursor.getString(_cursorIndexOfPhone);
            }
            final String _tmpEmail;
            if (_cursor.isNull(_cursorIndexOfEmail)) {
              _tmpEmail = null;
            } else {
              _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            }
            final String _tmpWebsite;
            if (_cursor.isNull(_cursorIndexOfWebsite)) {
              _tmpWebsite = null;
            } else {
              _tmpWebsite = _cursor.getString(_cursorIndexOfWebsite);
            }
            final String _tmpUsername;
            if (_cursor.isNull(_cursorIndexOfUsername)) {
              _tmpUsername = null;
            } else {
              _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            }
            final String _tmpThemeColorHex;
            if (_cursor.isNull(_cursorIndexOfThemeColorHex)) {
              _tmpThemeColorHex = null;
            } else {
              _tmpThemeColorHex = _cursor.getString(_cursorIndexOfThemeColorHex);
            }
            final boolean _tmpIsDarkTheme;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDarkTheme);
            _tmpIsDarkTheme = _tmp != 0;
            final boolean _tmpIsPublic;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsPublic);
            _tmpIsPublic = _tmp_1 != 0;
            final boolean _tmpIsPendingSync;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsPendingSync);
            _tmpIsPendingSync = _tmp_2 != 0;
            final String _tmpProfilePhotoLocalUri;
            if (_cursor.isNull(_cursorIndexOfProfilePhotoLocalUri)) {
              _tmpProfilePhotoLocalUri = null;
            } else {
              _tmpProfilePhotoLocalUri = _cursor.getString(_cursorIndexOfProfilePhotoLocalUri);
            }
            final String _tmpCompanyLogoLocalUri;
            if (_cursor.isNull(_cursorIndexOfCompanyLogoLocalUri)) {
              _tmpCompanyLogoLocalUri = null;
            } else {
              _tmpCompanyLogoLocalUri = _cursor.getString(_cursorIndexOfCompanyLogoLocalUri);
            }
            final String _tmpProfilePhotoUrl;
            if (_cursor.isNull(_cursorIndexOfProfilePhotoUrl)) {
              _tmpProfilePhotoUrl = null;
            } else {
              _tmpProfilePhotoUrl = _cursor.getString(_cursorIndexOfProfilePhotoUrl);
            }
            final String _tmpCompanyLogoUrl;
            if (_cursor.isNull(_cursorIndexOfCompanyLogoUrl)) {
              _tmpCompanyLogoUrl = null;
            } else {
              _tmpCompanyLogoUrl = _cursor.getString(_cursorIndexOfCompanyLogoUrl);
            }
            _result = new ProfileEntity(_tmpId,_tmpFullName,_tmpJobTitle,_tmpCompany,_tmpPhone,_tmpEmail,_tmpWebsite,_tmpUsername,_tmpThemeColorHex,_tmpIsDarkTheme,_tmpIsPublic,_tmpIsPendingSync,_tmpProfilePhotoLocalUri,_tmpCompanyLogoLocalUri,_tmpProfilePhotoUrl,_tmpCompanyLogoUrl);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getProfile(final Continuation<? super ProfileEntity> $completion) {
    final String _sql = "SELECT * FROM profile WHERE id = 'local_profile'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ProfileEntity>() {
      @Override
      @Nullable
      public ProfileEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFullName = CursorUtil.getColumnIndexOrThrow(_cursor, "fullName");
          final int _cursorIndexOfJobTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "jobTitle");
          final int _cursorIndexOfCompany = CursorUtil.getColumnIndexOrThrow(_cursor, "company");
          final int _cursorIndexOfPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "phone");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfWebsite = CursorUtil.getColumnIndexOrThrow(_cursor, "website");
          final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
          final int _cursorIndexOfThemeColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "themeColorHex");
          final int _cursorIndexOfIsDarkTheme = CursorUtil.getColumnIndexOrThrow(_cursor, "isDarkTheme");
          final int _cursorIndexOfIsPublic = CursorUtil.getColumnIndexOrThrow(_cursor, "isPublic");
          final int _cursorIndexOfIsPendingSync = CursorUtil.getColumnIndexOrThrow(_cursor, "isPendingSync");
          final int _cursorIndexOfProfilePhotoLocalUri = CursorUtil.getColumnIndexOrThrow(_cursor, "profilePhotoLocalUri");
          final int _cursorIndexOfCompanyLogoLocalUri = CursorUtil.getColumnIndexOrThrow(_cursor, "companyLogoLocalUri");
          final int _cursorIndexOfProfilePhotoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "profilePhotoUrl");
          final int _cursorIndexOfCompanyLogoUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "companyLogoUrl");
          final ProfileEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpFullName;
            if (_cursor.isNull(_cursorIndexOfFullName)) {
              _tmpFullName = null;
            } else {
              _tmpFullName = _cursor.getString(_cursorIndexOfFullName);
            }
            final String _tmpJobTitle;
            if (_cursor.isNull(_cursorIndexOfJobTitle)) {
              _tmpJobTitle = null;
            } else {
              _tmpJobTitle = _cursor.getString(_cursorIndexOfJobTitle);
            }
            final String _tmpCompany;
            if (_cursor.isNull(_cursorIndexOfCompany)) {
              _tmpCompany = null;
            } else {
              _tmpCompany = _cursor.getString(_cursorIndexOfCompany);
            }
            final String _tmpPhone;
            if (_cursor.isNull(_cursorIndexOfPhone)) {
              _tmpPhone = null;
            } else {
              _tmpPhone = _cursor.getString(_cursorIndexOfPhone);
            }
            final String _tmpEmail;
            if (_cursor.isNull(_cursorIndexOfEmail)) {
              _tmpEmail = null;
            } else {
              _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            }
            final String _tmpWebsite;
            if (_cursor.isNull(_cursorIndexOfWebsite)) {
              _tmpWebsite = null;
            } else {
              _tmpWebsite = _cursor.getString(_cursorIndexOfWebsite);
            }
            final String _tmpUsername;
            if (_cursor.isNull(_cursorIndexOfUsername)) {
              _tmpUsername = null;
            } else {
              _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            }
            final String _tmpThemeColorHex;
            if (_cursor.isNull(_cursorIndexOfThemeColorHex)) {
              _tmpThemeColorHex = null;
            } else {
              _tmpThemeColorHex = _cursor.getString(_cursorIndexOfThemeColorHex);
            }
            final boolean _tmpIsDarkTheme;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDarkTheme);
            _tmpIsDarkTheme = _tmp != 0;
            final boolean _tmpIsPublic;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsPublic);
            _tmpIsPublic = _tmp_1 != 0;
            final boolean _tmpIsPendingSync;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsPendingSync);
            _tmpIsPendingSync = _tmp_2 != 0;
            final String _tmpProfilePhotoLocalUri;
            if (_cursor.isNull(_cursorIndexOfProfilePhotoLocalUri)) {
              _tmpProfilePhotoLocalUri = null;
            } else {
              _tmpProfilePhotoLocalUri = _cursor.getString(_cursorIndexOfProfilePhotoLocalUri);
            }
            final String _tmpCompanyLogoLocalUri;
            if (_cursor.isNull(_cursorIndexOfCompanyLogoLocalUri)) {
              _tmpCompanyLogoLocalUri = null;
            } else {
              _tmpCompanyLogoLocalUri = _cursor.getString(_cursorIndexOfCompanyLogoLocalUri);
            }
            final String _tmpProfilePhotoUrl;
            if (_cursor.isNull(_cursorIndexOfProfilePhotoUrl)) {
              _tmpProfilePhotoUrl = null;
            } else {
              _tmpProfilePhotoUrl = _cursor.getString(_cursorIndexOfProfilePhotoUrl);
            }
            final String _tmpCompanyLogoUrl;
            if (_cursor.isNull(_cursorIndexOfCompanyLogoUrl)) {
              _tmpCompanyLogoUrl = null;
            } else {
              _tmpCompanyLogoUrl = _cursor.getString(_cursorIndexOfCompanyLogoUrl);
            }
            _result = new ProfileEntity(_tmpId,_tmpFullName,_tmpJobTitle,_tmpCompany,_tmpPhone,_tmpEmail,_tmpWebsite,_tmpUsername,_tmpThemeColorHex,_tmpIsDarkTheme,_tmpIsPublic,_tmpIsPendingSync,_tmpProfilePhotoLocalUri,_tmpCompanyLogoLocalUri,_tmpProfilePhotoUrl,_tmpCompanyLogoUrl);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
