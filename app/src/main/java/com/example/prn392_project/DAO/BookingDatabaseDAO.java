package com.example.prn392_project.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.prn392_project.Models.Booking;
import com.example.prn392_project.db.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingDatabaseDAO {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public BookingDatabaseDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * Lấy danh sách booking (đang chờ hoặc đã hoàn thành) cho một nghệ sĩ
     * và JOIN với bảng User để lấy tên khách hàng.
     * (Đây là hàm đã được sửa lỗi)
     */
    public List<Booking> getBookingsByArtist(int artistId, String status) {
        List<Booking> bookings = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        // Tên cột tạm thời cho JOIN
        String customerNameCol = "customer_full_name";

        // Câu lệnh JOIN phức tạp
        String query = "SELECT b.*, u." + DatabaseHelper.COLUMN_USER_FULL_NAME + " AS " + customerNameCol +
                " FROM " + DatabaseHelper.TABLE_BOOKINGS + " b" +
                " LEFT JOIN " + DatabaseHelper.TABLE_USERS + " u ON b." + DatabaseHelper.COLUMN_BOOKING_CUSTOMER_ID + " = u." + DatabaseHelper.COLUMN_USER_ID +
                " WHERE b." + DatabaseHelper.COLUMN_BOOKING_ARTIST_ID + " = ?" +
                " AND b." + DatabaseHelper.COLUMN_BOOKING_STATUS + " = ?" +
                " ORDER BY b." + DatabaseHelper.COLUMN_BOOKING_START_TIME + " DESC";

        try {
            cursor = db.rawQuery(query, new String[]{String.valueOf(artistId), status});

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // *** SỬA LỖI TẠI ĐÂY ***
                    // Gọi hàm trợ giúp đúng
                    bookings.add(cursorToBookingForArtist(cursor, customerNameCol));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return bookings;
    }

    /**
     * Lấy TẤT CẢ các booking của một khách hàng (Dùng cho màn hình Lịch đặt của tôi)
     */
    public List<Booking> getBookingsByCustomer(int customerId) {
        List<Booking> bookings = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        String tableBookings = " b";
        String tableProfiles = " p";
        String tableReviews = " r";
        String artistNameCol = "artist_stage_name";
        String isReviewedCol = "is_reviewed";

        String query = "SELECT b.*, p." + DatabaseHelper.COLUMN_PROFILE_STAGE_NAME + " AS " + artistNameCol + "," +
                " CASE WHEN r." + DatabaseHelper.COLUMN_REVIEW_ID + " IS NOT NULL THEN 1 ELSE 0 END AS " + isReviewedCol +
                " FROM " + DatabaseHelper.TABLE_BOOKINGS + tableBookings +
                " JOIN " + DatabaseHelper.TABLE_ARTIST_PROFILES + tableProfiles + " ON b." + DatabaseHelper.COLUMN_BOOKING_ARTIST_ID + " = p." + DatabaseHelper.COLUMN_PROFILE_USER_ID +
                " LEFT JOIN " + DatabaseHelper.TABLE_REVIEWS + tableReviews + " ON b." + DatabaseHelper.COLUMN_BOOKING_ID + " = r." + DatabaseHelper.COLUMN_REVIEW_BOOKING_ID +
                " WHERE b." + DatabaseHelper.COLUMN_BOOKING_CUSTOMER_ID + " = ?" +
                " ORDER BY b." + DatabaseHelper.COLUMN_BOOKING_START_TIME + " DESC";

        try {
            cursor = db.rawQuery(query, new String[]{String.valueOf(customerId)});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Gọi hàm trợ giúp cho Customer
                    bookings.add(cursorToBookingForCustomer(cursor, artistNameCol, isReviewedCol));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return bookings;
    }

    // --- CÁC HÀM CẬP NHẬT VÀ THÊM MỚI ---

    /**
     * Khách hàng tạo một yêu cầu đặt lịch mới
     */
    public boolean createBookingRequest(int customerId, int artistId, String eventTitle,
                                        String eventLocation, String startTime, String endTime,
                                        double calculatedPrice) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_BOOKING_CUSTOMER_ID, customerId);
        values.put(DatabaseHelper.COLUMN_BOOKING_ARTIST_ID, artistId);
        values.put(DatabaseHelper.COLUMN_BOOKING_EVENT_TITLE, eventTitle);
        values.put(DatabaseHelper.COLUMN_BOOKING_EVENT_LOCATION, eventLocation);
        values.put(DatabaseHelper.COLUMN_BOOKING_START_TIME, startTime);
        values.put(DatabaseHelper.COLUMN_BOOKING_END_TIME, endTime);
        values.put(DatabaseHelper.COLUMN_BOOKING_PRICE, calculatedPrice);
        values.put(DatabaseHelper.COLUMN_BOOKING_STATUS, "pending"); // Trạng thái ban đầu
        values.put(DatabaseHelper.COLUMN_BOOKING_CREATED_AT,
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date()));

        long result = -1;
        try {
            result = db.insert(DatabaseHelper.TABLE_BOOKINGS, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result != -1;
    }

    /**
     * Chỉ cập nhật status (dùng cho Từ chối hoặc Hủy)
     */
    public boolean updateBookingStatus(int bookingId, String newStatus) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_BOOKING_STATUS, newStatus);

        String selection = DatabaseHelper.COLUMN_BOOKING_ID + " = ?";
        String[] selectionArgs = { String.valueOf(bookingId) };

        int rowsAffected = 0;
        try {
            rowsAffected = db.update(DatabaseHelper.TABLE_BOOKINGS, values, selection, selectionArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowsAffected > 0;
    }

    /**
     * (Logic nghiệp vụ) Chấp nhận một yêu cầu và tự động xử lý các xung đột.
     */
    public boolean acceptBookingRequest(int bookingId, int artistId, String startTime, String endTime) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction(); // Bắt đầu Transaction

        try {
            // Bước 1: Chấp nhận yêu cầu
            ContentValues acceptedValues = new ContentValues();
            acceptedValues.put(DatabaseHelper.COLUMN_BOOKING_STATUS, "confirmed");
            String selection = DatabaseHelper.COLUMN_BOOKING_ID + " = ?";
            int rowsAffected = db.update(DatabaseHelper.TABLE_BOOKINGS, acceptedValues, selection, new String[]{String.valueOf(bookingId)});

            if (rowsAffected == 0) {
                db.endTransaction();
                return false; // Lỗi, không cập nhật được
            }

            // Bước 2: Đánh dấu lịch rảnh là đã được đặt
            String availSelection = DatabaseHelper.COLUMN_AVAIL_ARTIST_ID + " = ? AND " +
                    DatabaseHelper.COLUMN_AVAIL_START_TIME + " = ? AND " +
                    DatabaseHelper.COLUMN_AVAIL_END_TIME + " = ? AND " +
                    DatabaseHelper.COLUMN_AVAIL_IS_BOOKED + " = 0";

            ContentValues availValues = new ContentValues();
            availValues.put(DatabaseHelper.COLUMN_AVAIL_IS_BOOKED, 1);
            db.update(DatabaseHelper.TABLE_ARTIST_AVAILABILITIES, availValues, availSelection,
                    new String[]{String.valueOf(artistId), startTime, endTime});

            // Bước 3: Tự động HỦY các yêu cầu 'pending' khác bị trùng lịch
            ContentValues cancelledValues = new ContentValues();
            cancelledValues.put(DatabaseHelper.COLUMN_BOOKING_STATUS, "cancelled");

            String conflictSelection = DatabaseHelper.COLUMN_BOOKING_ARTIST_ID + " = ? AND " +
                    DatabaseHelper.COLUMN_BOOKING_STATUS + " = 'pending' AND " +
                    DatabaseHelper.COLUMN_BOOKING_ID + " != ? AND " +
                    DatabaseHelper.COLUMN_BOOKING_START_TIME + " = ? AND " +
                    DatabaseHelper.COLUMN_BOOKING_END_TIME + " = ?";

            int cancelledRows = db.update(DatabaseHelper.TABLE_BOOKINGS, cancelledValues, conflictSelection,
                    new String[]{
                            String.valueOf(artistId),
                            String.valueOf(bookingId),
                            startTime,
                            endTime
                    });

            Log.d("BookingDAO", "Đã tự động hủy " + cancelledRows + " yêu cầu trùng lặp.");

            db.setTransactionSuccessful();
            return true;

        } catch (Exception e) {
            Log.e("BookingDAO", "Lỗi khi chấp nhận booking (transaction)", e);
            return false;
        } finally {
            db.endTransaction();
        }
    }

    /**
     * THÊM MỚI: Đếm số lượng yêu cầu đặt lịch đang chờ (pending) của một nghệ sĩ
     */
    public int getPendingBookingsCount(int artistId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        int count = 0;

        try {
            String query = "SELECT COUNT(" + DatabaseHelper.COLUMN_BOOKING_ID + ") FROM " +
                    DatabaseHelper.TABLE_BOOKINGS +
                    " WHERE " + DatabaseHelper.COLUMN_BOOKING_ARTIST_ID + " = ? AND " +
                    DatabaseHelper.COLUMN_BOOKING_STATUS + " = 'pending'";

            cursor = db.rawQuery(query, new String[]{String.valueOf(artistId)});

            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0); // Lấy giá trị từ cột COUNT
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }


    // --- HÀM TRỢ GIÚP CURSOR (Đã tách riêng) ---

    /**
     * Hàm helper cho Artist (lấy Tên Khách hàng)
     */
    private Booking cursorToBookingForArtist(Cursor cursor, String customerNameColAlias) {
        Booking booking = new Booking(); // Sử dụng constructor rỗng
        booking.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_ID)));
        booking.setCustomerId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_CUSTOMER_ID)));
        booking.setArtistId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_ARTIST_ID)));
        booking.setEventTitle(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_EVENT_TITLE)));
        booking.setEventLocation(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_EVENT_LOCATION)));
        booking.setStartTime(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_START_TIME)));
        booking.setEndTime(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_END_TIME)));
        booking.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_STATUS)));
        booking.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_PRICE)));

        // Lấy tên khách hàng từ JOIN
        String customerName = "Khách hàng (đã xóa)";
        int nameIndex = cursor.getColumnIndex(customerNameColAlias);
        if (nameIndex != -1 && !cursor.isNull(nameIndex)) {
            customerName = cursor.getString(nameIndex);
        }
        booking.setCustomerName(customerName);

        return booking;
    }

    /**
     * Hàm helper cho Customer (lấy Tên Nghệ sĩ và Trạng thái Đánh giá)
     */
    private Booking cursorToBookingForCustomer(Cursor cursor, String artistNameColAlias, String isReviewedColAlias) {
        Booking booking = new Booking(); // Sử dụng constructor rỗng
        booking.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_ID)));
        booking.setCustomerId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_CUSTOMER_ID)));
        booking.setArtistId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_ARTIST_ID)));
        booking.setEventTitle(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_EVENT_TITLE)));
        booking.setEventLocation(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_EVENT_LOCATION)));
        booking.setStartTime(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_START_TIME)));
        booking.setEndTime(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_END_TIME)));
        booking.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_STATUS)));
        booking.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_PRICE)));

        // Lấy tên nghệ sĩ
        String artistName = "Nghệ sĩ không xác định";
        int artistNameIndex = cursor.getColumnIndex(artistNameColAlias);
        if (artistNameIndex != -1 && !cursor.isNull(artistNameIndex)) {
            artistName = cursor.getString(artistNameIndex);
        }
        booking.setArtistName(artistName);

        // Lấy trạng thái đã review
        boolean isReviewed = false;
        int isReviewedIndex = cursor.getColumnIndex(isReviewedColAlias);
        if (isReviewedIndex != -1) {
            isReviewed = cursor.getInt(isReviewedIndex) == 1;
        }
        booking.setReviewed(isReviewed);

        return booking;
    }
}