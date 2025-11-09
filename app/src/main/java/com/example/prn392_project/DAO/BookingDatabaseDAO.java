package com.example.prn392_project.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.prn392_project.Models.Booking;
import com.example.prn392_project.db.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

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
     */
    public List<Booking> getBookingsByArtist(int artistId, String status) {
        List<Booking> bookings = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        // Câu lệnh JOIN phức tạp
        String query = "SELECT b.*, u." + DatabaseHelper.COLUMN_USER_FULL_NAME +
                " FROM " + DatabaseHelper.TABLE_BOOKINGS + " b" +
                " LEFT JOIN " + DatabaseHelper.TABLE_USERS + " u ON b." + DatabaseHelper.COLUMN_BOOKING_CUSTOMER_ID + " = u." + DatabaseHelper.COLUMN_USER_ID +
                " WHERE b." + DatabaseHelper.COLUMN_BOOKING_ARTIST_ID + " = ?" +
                " AND b." + DatabaseHelper.COLUMN_BOOKING_STATUS + " = ?" +
                " ORDER BY b." + DatabaseHelper.COLUMN_BOOKING_START_TIME + " DESC";

        try {
            cursor = db.rawQuery(query, new String[]{String.valueOf(artistId), status});

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    bookings.add(cursorToBooking(cursor));
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
     * Đây là logic nghiệp vụ quan trọng.
     * Chấp nhận một yêu cầu và tự động xử lý các xung đột.
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
                return false; // Lỗi, không cập nhật được
            }

            // Bước 2: (Như bạn yêu cầu) Xóa lịch rảnh của nghệ sĩ tại khung giờ đó
            // Giả định booking time = availability time
            String availSelection = DatabaseHelper.COLUMN_AVAIL_ARTIST_ID + " = ? AND " +
                    DatabaseHelper.COLUMN_AVAIL_START_TIME + " = ? AND " +
                    DatabaseHelper.COLUMN_AVAIL_END_TIME + " = ? AND " +
                    DatabaseHelper.COLUMN_AVAIL_IS_BOOKED + " = 0";

            // Thay vì xóa, chúng ta nên đánh dấu là đã đặt (is_booked = 1)
            // Nếu bạn muốn XÓA HẲN, dùng db.delete()
            ContentValues availValues = new ContentValues();
            availValues.put(DatabaseHelper.COLUMN_AVAIL_IS_BOOKED, 1);

            db.update(DatabaseHelper.TABLE_ARTIST_AVAILABILITIES, availValues, availSelection,
                    new String[]{String.valueOf(artistId), startTime, endTime});

            // (Bạn cũng có thể dùng db.delete() nếu muốn)
            // db.delete(DatabaseHelper.TABLE_ARTIST_AVAILABILITIES, availSelection,
            //           new String[]{String.valueOf(artistId), startTime, endTime});


            // Bước 3: (Như bạn yêu cầu) Tự động HỦY các yêu cầu khác bị trùng lịch
            // Tìm tất cả các booking 'pending' khác của cùng nghệ sĩ,
            // có khung giờ *chính xác* giống hệt (để đơn giản)
            ContentValues cancelledValues = new ContentValues();
            cancelledValues.put(DatabaseHelper.COLUMN_BOOKING_STATUS, "cancelled"); // Hoặc 'rejected'

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

            // Nếu tất cả thành công
            db.setTransactionSuccessful();
            return true;

        } catch (Exception e) {
            Log.e("BookingDAO", "Lỗi khi chấp nhận booking (transaction)", e);
            return false;
        } finally {
            db.endTransaction(); // Kết thúc Transaction (commit nếu successful, rollback nếu không)
        }
    }


    private Booking cursorToBooking(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_ID));
        int customerId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_CUSTOMER_ID));
        int artistId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_ARTIST_ID));
        String eventTitle = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_EVENT_TITLE));
        String eventLocation = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_EVENT_LOCATION));
        String startTime = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_START_TIME));
        String endTime = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_END_TIME));
        String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_STATUS));
        double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_PRICE));

        // Lấy từ cột JOIN
        String customerName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_FULL_NAME));
        if (customerName == null) {
            customerName = "Khách hàng (đã xóa)";
        }

        return new Booking(id, customerId, artistId, eventTitle, eventLocation, startTime, endTime, status, price, customerName);
    }
}
