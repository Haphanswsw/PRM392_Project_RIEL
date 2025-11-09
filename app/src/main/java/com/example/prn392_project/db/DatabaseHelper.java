package com.example.prn392_project.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

/**
 * Lớp trợ giúp quản lý việc tạo và nâng cấp cơ sở dữ liệu SQLite.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Thông tin cơ sở dữ liệu
    private static final String DATABASE_NAME = "ArtistBooking.db";
    private static final int DATABASE_VERSION = 1;

    // Bảng users
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PASSWORD_HASH = "password_hash";
    public static final String COLUMN_USER_FULL_NAME = "full_name";
    public static final String COLUMN_USER_PHONE = "phone";
    public static final String COLUMN_USER_ROLE = "role"; // 'artist', 'customer', 'admin'
    public static final String COLUMN_USER_BIO = "bio";
    public static final String COLUMN_USER_AVATAR_URL = "avatar_url";
    public static final String COLUMN_USER_CREATED_AT = "created_at";
    public static final String COLUMN_USER_STATUS = "status";

    // Bảng artist_profiles
    public static final String TABLE_ARTIST_PROFILES = "artist_profiles";
    public static final String COLUMN_PROFILE_ID = "id";
    public static final String COLUMN_PROFILE_USER_ID = "user_id"; // FK -> users
    public static final String COLUMN_PROFILE_STAGE_NAME = "stage_name";
    public static final String COLUMN_PROFILE_GENRES = "genres"; // Lưu dạng text (JSON hoặc CSV)
    public static final String COLUMN_PROFILE_PRICE_PER_HOUR = "price_per_hour";
    public static final String COLUMN_PROFILE_LOCATION = "location";
    public static final String COLUMN_PROFILE_RATING_AVG = "rating_avg";
    public static final String COLUMN_PROFILE_EXPERIENCE_YEARS = "experience_years";
    public static final String COLUMN_PROFILE_SOCIAL_LINKS = "social_links"; // Lưu dạng TEXT (JSON)

    // Bảng artist_availabilities
    public static final String TABLE_ARTIST_AVAILABILITIES = "artist_availabilities";
    public static final String COLUMN_AVAIL_ID = "id";
    public static final String COLUMN_AVAIL_ARTIST_ID = "artist_id"; // FK -> users
    public static final String COLUMN_AVAIL_START_TIME = "start_time"; // ISO 8601 TEXT
    public static final String COLUMN_AVAIL_END_TIME = "end_time"; // ISO 8601 TEXT
    public static final String COLUMN_AVAIL_IS_BOOKED = "is_booked"; // INTEGER 0=false, 1=true
    public static final String COLUMN_AVAIL_CREATED_AT = "created_at";

    // Bảng bookings
    public static final String TABLE_BOOKINGS = "bookings";
    public static final String COLUMN_BOOKING_ID = "id";
    public static final String COLUMN_BOOKING_CUSTOMER_ID = "customer_id"; // FK -> users
    public static final String COLUMN_BOOKING_ARTIST_ID = "artist_id"; // FK -> users
    public static final String COLUMN_BOOKING_EVENT_TITLE = "event_title";
    public static final String COLUMN_BOOKING_EVENT_LOCATION = "event_location";
    public static final String COLUMN_BOOKING_START_TIME = "start_time";
    public static final String COLUMN_BOOKING_END_TIME = "end_time";
    public static final String COLUMN_BOOKING_STATUS = "status"; // pending, confirmed, cancelled, completed
    public static final String COLUMN_BOOKING_PRICE = "price";
    public static final String COLUMN_BOOKING_CREATED_AT = "created_at";
    public static final String COLUMN_BOOKING_UPDATED_AT = "updated_at";

    // Bảng payments
    public static final String TABLE_PAYMENTS = "payments";
    public static final String COLUMN_PAYMENT_ID = "id";
    public static final String COLUMN_PAYMENT_BOOKING_ID = "booking_id"; // FK -> bookings
    public static final String COLUMN_PAYMENT_AMOUNT = "amount";
    public static final String COLUMN_PAYMENT_CURRENCY = "currency";
    public static final String COLUMN_PAYMENT_METHOD = "method";
    public static final String COLUMN_PAYMENT_STATUS = "status"; // pending, success, failed, refunded
    public static final String COLUMN_PAYMENT_TRANSACTION_REF = "transaction_ref";
    public static final String COLUMN_PAYMENT_PAID_AT = "paid_at";

    // Bảng reviews
    public static final String TABLE_REVIEWS = "reviews";
    public static final String COLUMN_REVIEW_ID = "id";
    public static final String COLUMN_REVIEW_BOOKING_ID = "booking_id"; // FK -> bookings
    public static final String COLUMN_REVIEW_CUSTOMER_ID = "customer_id"; // FK -> users
    public static final String COLUMN_REVIEW_ARTIST_ID = "artist_id"; // FK -> users
    public static final String COLUMN_REVIEW_RATING = "rating"; // INTEGER 1-5
    public static final String COLUMN_REVIEW_COMMENT = "comment";
    public static final String COLUMN_REVIEW_CREATED_AT = "created_at";

    // Bảng messages (optional)
    public static final String TABLE_MESSAGES = "messages";
    public static final String COLUMN_MESSAGE_ID = "id";
    public static final String COLUMN_MESSAGE_SENDER_ID = "sender_id"; // FK -> users
    public static final String COLUMN_MESSAGE_RECEIVER_ID = "receiver_id"; // FK -> users
    public static final String COLUMN_MESSAGE_BOOKING_ID = "booking_id"; // FK -> bookings (optional)
    public static final String COLUMN_MESSAGE_CONTENT = "content";
    public static final String COLUMN_MESSAGE_SENT_AT = "sent_at";

    // Bảng notifications
    public static final String TABLE_NOTIFICATIONS = "notifications";
    public static final String COLUMN_NOTIF_ID = "id";
    public static final String COLUMN_NOTIF_USER_ID = "user_id"; // FK -> users
    public static final String COLUMN_NOTIF_TYPE = "type";
    public static final String COLUMN_NOTIF_MESSAGE = "message";
    public static final String COLUMN_NOTIF_IS_READ = "is_read"; // INTEGER 0=false, 1=true
    public static final String COLUMN_NOTIF_CREATED_AT = "created_at";

    // Bảng audit_logs
    public static final String TABLE_AUDIT_LOGS = "audit_logs";
    public static final String COLUMN_AUDIT_ID = "id";
    public static final String COLUMN_AUDIT_USER_ID = "user_id"; // FK -> users (who did it)
    public static final String COLUMN_AUDIT_ACTION = "audit_action";
    public static final String COLUMN_AUDIT_TARGET_TABLE = "target_table";
    public static final String COLUMN_AUDIT_TARGET_ID = "target_id";
    public static final String COLUMN_AUDIT_CREATED_AT = "created_at";


    // --- Các câu lệnh TẠO BẢNG (CREATE TABLE) ---

    private static final String SQL_CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_USER_EMAIL + " TEXT NOT NULL UNIQUE," +
                    COLUMN_USER_PASSWORD_HASH + " TEXT NOT NULL," +
                    COLUMN_USER_FULL_NAME + " TEXT," +
                    COLUMN_USER_PHONE + " TEXT," +
                    COLUMN_USER_ROLE + " TEXT NOT NULL CHECK(" + COLUMN_USER_ROLE + " IN ('artist', 'customer', 'admin'))," +
                    COLUMN_USER_BIO + " TEXT," +
                    COLUMN_USER_AVATAR_URL + " TEXT," +
                    COLUMN_USER_STATUS + " TEXT DEFAULT 'active'," +
                    COLUMN_USER_CREATED_AT + " TEXT DEFAULT CURRENT_TIMESTAMP" +
                    ")";

    private static final String SQL_CREATE_TABLE_ARTIST_PROFILES =
            "CREATE TABLE " + TABLE_ARTIST_PROFILES + " (" +
                    COLUMN_PROFILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_PROFILE_USER_ID + " INTEGER NOT NULL UNIQUE," + // Một user chỉ có 1 profile
                    COLUMN_PROFILE_STAGE_NAME + " TEXT," +
                    COLUMN_PROFILE_GENRES + " TEXT," +
                    COLUMN_PROFILE_PRICE_PER_HOUR + " REAL," +
                    COLUMN_PROFILE_LOCATION + " TEXT," +
                    COLUMN_PROFILE_RATING_AVG + " REAL DEFAULT 0.0," +
                    COLUMN_PROFILE_EXPERIENCE_YEARS + " INTEGER DEFAULT 0," +
                    COLUMN_PROFILE_SOCIAL_LINKS + " TEXT," +
                    "FOREIGN KEY(" + COLUMN_PROFILE_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE" +
                    ")";

    private static final String SQL_CREATE_TABLE_ARTIST_AVAILABILITIES =
            "CREATE TABLE " + TABLE_ARTIST_AVAILABILITIES + " (" +
                    COLUMN_AVAIL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_AVAIL_ARTIST_ID + " INTEGER NOT NULL," +
                    COLUMN_AVAIL_START_TIME + " TEXT NOT NULL," +
                    COLUMN_AVAIL_END_TIME + " TEXT NOT NULL," +
                    COLUMN_AVAIL_IS_BOOKED + " INTEGER DEFAULT 0," +
                    COLUMN_AVAIL_CREATED_AT + " TEXT DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY(" + COLUMN_AVAIL_ARTIST_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE" +
                    ")";

    private static final String SQL_CREATE_TABLE_BOOKINGS =
            "CREATE TABLE " + TABLE_BOOKINGS + " (" +
                    COLUMN_BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_BOOKING_CUSTOMER_ID + " INTEGER NOT NULL," +
                    COLUMN_BOOKING_ARTIST_ID + " INTEGER NOT NULL," +
                    COLUMN_BOOKING_EVENT_TITLE + " TEXT," +
                    COLUMN_BOOKING_EVENT_LOCATION + " TEXT," +
                    COLUMN_BOOKING_START_TIME + " TEXT NOT NULL," +
                    COLUMN_BOOKING_END_TIME + " TEXT NOT NULL," +
                    COLUMN_BOOKING_STATUS + " TEXT NOT NULL DEFAULT 'pending'," +
                    COLUMN_BOOKING_PRICE + " REAL," +
                    COLUMN_BOOKING_CREATED_AT + " TEXT DEFAULT CURRENT_TIMESTAMP," +
                    COLUMN_BOOKING_UPDATED_AT + " TEXT," +
                    "FOREIGN KEY(" + COLUMN_BOOKING_CUSTOMER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ") ON DELETE SET NULL," +
                    "FOREIGN KEY(" + COLUMN_BOOKING_ARTIST_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ") ON DELETE SET NULL" +
                    ")";

    private static final String SQL_CREATE_TABLE_PAYMENTS =
            "CREATE TABLE " + TABLE_PAYMENTS + " (" +
                    COLUMN_PAYMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_PAYMENT_BOOKING_ID + " INTEGER NOT NULL," +
                    COLUMN_PAYMENT_AMOUNT + " REAL NOT NULL," +
                    COLUMN_PAYMENT_CURRENCY + " TEXT DEFAULT 'VND'," +
                    COLUMN_PAYMENT_METHOD + " TEXT," +
                    COLUMN_PAYMENT_STATUS + " TEXT NOT NULL," +
                    COLUMN_PAYMENT_TRANSACTION_REF + " TEXT," +
                    COLUMN_PAYMENT_PAID_AT + " TEXT," +
                    "FOREIGN KEY(" + COLUMN_PAYMENT_BOOKING_ID + ") REFERENCES " + TABLE_BOOKINGS + "(" + COLUMN_BOOKING_ID + ") ON DELETE CASCADE" +
                    ")";

    private static final String SQL_CREATE_TABLE_REVIEWS =
            "CREATE TABLE " + TABLE_REVIEWS + " (" +
                    COLUMN_REVIEW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_REVIEW_BOOKING_ID + " INTEGER NOT NULL UNIQUE," + // 1 booking chỉ có 1 review
                    COLUMN_REVIEW_CUSTOMER_ID + " INTEGER NOT NULL," +
                    COLUMN_REVIEW_ARTIST_ID + " INTEGER NOT NULL," +
                    COLUMN_REVIEW_RATING + " INTEGER NOT NULL CHECK(" + COLUMN_REVIEW_RATING + " >= 1 AND " + COLUMN_REVIEW_RATING + " <= 5)," +
                    COLUMN_REVIEW_COMMENT + " TEXT," +
                    COLUMN_REVIEW_CREATED_AT + " TEXT DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY(" + COLUMN_REVIEW_BOOKING_ID + ") REFERENCES " + TABLE_BOOKINGS + "(" + COLUMN_BOOKING_ID + ") ON DELETE CASCADE," +
                    "FOREIGN KEY(" + COLUMN_REVIEW_CUSTOMER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ") ON DELETE SET NULL," +
                    "FOREIGN KEY(" + COLUMN_REVIEW_ARTIST_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ") ON DELETE SET NULL" +
                    ")";

    private static final String SQL_CREATE_TABLE_MESSAGES =
            "CREATE TABLE " + TABLE_MESSAGES + " (" +
                    COLUMN_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_MESSAGE_SENDER_ID + " INTEGER NOT NULL," +
                    COLUMN_MESSAGE_RECEIVER_ID + " INTEGER NOT NULL," +
                    COLUMN_MESSAGE_BOOKING_ID + " INTEGER," +
                    COLUMN_MESSAGE_CONTENT + " TEXT," +
                    COLUMN_MESSAGE_SENT_AT + " TEXT DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY(" + COLUMN_MESSAGE_SENDER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE," +
                    "FOREIGN KEY(" + COLUMN_MESSAGE_RECEIVER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE," +
                    "FOREIGN KEY(" + COLUMN_MESSAGE_BOOKING_ID + ") REFERENCES " + TABLE_BOOKINGS + "(" + COLUMN_BOOKING_ID + ") ON DELETE SET NULL" +
                    ")";

    private static final String SQL_CREATE_TABLE_NOTIFICATIONS =
            "CREATE TABLE " + TABLE_NOTIFICATIONS + " (" +
                    COLUMN_NOTIF_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_NOTIF_USER_ID + " INTEGER NOT NULL," +
                    COLUMN_NOTIF_TYPE + " TEXT," +
                    COLUMN_NOTIF_MESSAGE + " TEXT NOT NULL," +
                    COLUMN_NOTIF_IS_READ + " INTEGER DEFAULT 0," +
                    COLUMN_NOTIF_CREATED_AT + " TEXT DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY(" + COLUMN_NOTIF_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE" +
                    ")";

    private static final String SQL_CREATE_TABLE_AUDIT_LOGS =
            "CREATE TABLE " + TABLE_AUDIT_LOGS + " (" +
                    COLUMN_AUDIT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_AUDIT_USER_ID + " INTEGER," +
                    COLUMN_AUDIT_ACTION + " TEXT NOT NULL," +
                    COLUMN_AUDIT_TARGET_TABLE + " TEXT," +
                    COLUMN_AUDIT_TARGET_ID + " INTEGER," +
                    COLUMN_AUDIT_CREATED_AT + " TEXT DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY(" + COLUMN_AUDIT_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ") ON DELETE SET NULL" +
                    ")";


    /**
     * Constructor
     * @param context
     */
    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Bật hỗ trợ Foreign Key.
     * Được gọi khi cơ sở dữ liệu được cấu hình, sau khi được tạo/mở.
     */
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    /**
     * Được gọi khi cơ sở dữ liệu được tạo lần đầu tiên.
     * @param db Đối tượng SQLiteDatabase.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Bật foreign keys (nếu không dùng onConfigure cho API < 16)
        // db.execSQL("PRAGMA foreign_keys=ON;");

        // Thực thi các câu lệnh tạo bảng
        db.execSQL(SQL_CREATE_TABLE_USERS);
        db.execSQL(SQL_CREATE_TABLE_ARTIST_PROFILES);
        db.execSQL(SQL_CREATE_TABLE_ARTIST_AVAILABILITIES);
        db.execSQL(SQL_CREATE_TABLE_BOOKINGS);
        db.execSQL(SQL_CREATE_TABLE_PAYMENTS);
        db.execSQL(SQL_CREATE_TABLE_REVIEWS);
        db.execSQL(SQL_CREATE_TABLE_MESSAGES);
        db.execSQL(SQL_CREATE_TABLE_NOTIFICATIONS);
        db.execSQL(SQL_CREATE_TABLE_AUDIT_LOGS);
    }

    /**
     * Được gọi khi cần nâng cấp cơ sở dữ liệu (thay đổi DATABASE_VERSION).
     * @param db Cơ sở dữ liệu.
     * @param oldVersion Phiên bản cũ.
     * @param newVersion Phiên bản mới.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Chiến lược di chuyển (migration) đơn giản: Xóa tất cả và tạo lại.
        // Đối với ứng dụng sản phẩm, bạn sẽ cần các lệnh ALTER TABLE cẩn thận.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUDIT_LOGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REVIEWS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAYMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTIST_AVAILABILITIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTIST_PROFILES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        // Tạo lại cơ sở dữ liệu
        onCreate(db);
    }

    /**
     * Được gọi khi cần hạ cấp cơ sở dữ liệu.
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
