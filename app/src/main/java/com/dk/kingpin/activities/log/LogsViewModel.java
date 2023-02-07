
package com.dk.kingpin.activities.log;


import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.dk.kingpin.activities.log.database.LogsRepository;
import com.dk.kingpin.model.Logs;

import java.util.List;

public class LogsViewModel extends AndroidViewModel {
    private LogsRepository logsRepository;
    private final LiveData<List<Logs>> mLogsList;

    /**
     * @param application
     */
    public LogsViewModel(Application application) {
        super(application);
        logsRepository = new LogsRepository(application);
        mLogsList = logsRepository.getLogs();
    }

    public LiveData<List<Logs>> getmLogsList() {
        return mLogsList;
    }

    public void clearLogs() {
        logsRepository.clearLogs();
    }


}

