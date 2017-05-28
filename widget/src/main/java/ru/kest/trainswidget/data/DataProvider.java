package ru.kest.trainswidget.data;

import android.location.Location;

import java.util.List;

import ru.kest.trainswidget.model.domain.NearestStation;
import ru.kest.trainswidget.model.domain.TrainThread;

/**
 * Created by KKharitonov on 14.02.2016.
 */
public interface DataProvider {

    List<TrainThread> getTrainsFromHomeToWork();
    void setTrainsFromHomeToWork(List<TrainThread> trainsFromHomeToWork);

    List<TrainThread> getTrainsFromWorkToHome();
    void setTrainsFromWorkToHome(List<TrainThread> trainsFromWorkToHome);

    boolean isSetTrainThreads();
    TrainThread getThreadByHash(int hash);

    Location getLastLocation();
    void setLastLocation(Location location);
    boolean isSetLastLocation();

    TrainThread getNotificationTrain();
    void setNotificationTrain(TrainThread notificationTrain);
    boolean isSetNotificationTrain();

    NearestStation getNearestStation();
}
