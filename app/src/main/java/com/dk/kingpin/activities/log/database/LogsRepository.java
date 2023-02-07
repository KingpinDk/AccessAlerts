

package com.dk.kingpin.activities.log.database;



import android.app.Application;

import androidx.lifecycle.LiveData;

import com.dk.kingpin.model.Logs;

import java.util.List;

public class LogsRepository {

    private LogsDao logsDao;
    private LiveData<List<Logs>> logLiveData;

    /**
     * @param application
     */
    public LogsRepository(Application application) {
        LogsRoomDatabase db = LogsRoomDatabase.getDatabase(application);
        logsDao = db.logsDao();
        logLiveData = logsDao.getOrderedLogs();
    }

    /**
     * @return
     */
    public LiveData<List<Logs>> getLogs() {
        // Room executes all queries on a separate thread.
        // Observed LiveData will notify the observer when the data has changed.
        return logLiveData;
    }


    /**
     * @param logs
     */
    public void insertLog(Logs logs) {
        // You must call this on a non-UI thread or your app will throw an exception. Room ensures
        // that you're not doing any long running operations on the main thread, blocking the UI.
        LogsRoomDatabase.databaseWriteExecutor.execute(() -> {
            logsDao.insert(logs);
        });
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void clearLogs() {
        LogsRoomDatabase.databaseWriteExecutor.execute(() -> {
            logsDao.clearAllLogs();
        });
    }
}

