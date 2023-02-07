

package com.dk.kingpin.activities.log.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.dk.kingpin.model.Logs;

import java.util.List;

@Dao
public interface LogsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Logs logs);

    @Query("DELETE FROM logs_database")
    void clearAllLogs();

    @Query("SELECT * FROM logs_database ORDER BY timestamp DESC")
    LiveData<List<Logs>> getOrderedLogs();
}
