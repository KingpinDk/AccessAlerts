

package com.dk.kingpin.activities.log.database;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.dk.kingpin.model.Logs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Database(entities = {Logs.class}, version = 5, exportSchema = true)
public abstract class LogsRoomDatabase extends RoomDatabase {
    private static final int NUMBER_OF_THREADS = 4;

    public abstract LogsDao logsDao();

    private static volatile LogsRoomDatabase INSTANCE;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);


    static LogsRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (LogsRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            LogsRoomDatabase.class, "logs.db")
                            .addCallback(sRoomDatabaseCallback)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };
}
