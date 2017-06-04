package ru.kest.trainswidget.converters

import ru.kest.trainswidget.model.domain.TrainComfortLevel
import ru.kest.trainswidget.model.domain.TrainThread
import ru.kest.trainswidget.model.yandex.ScheduleResponse
import java.util.*

/**
 * DTO to Model converter
 *
 * Created by Konstantin on 26.05.2017.
 */
object YandexToDomainConverter {

    fun scheduleResponseToDomain(trainScheduleResponse: ScheduleResponse?): List<TrainThread> {
        val result = ArrayList<TrainThread>()
        if (trainScheduleResponse != null && trainScheduleResponse.threads.isNotEmpty()) {
            for ((arrival, departure, _, thread) in trainScheduleResponse.threads) {
                if (departure!!.before(Date())) {
                    continue
                }

                val domainThread = TrainThread(arrival = arrival!!, departure = departure, title = thread?.shortTitle ?: "")
                if (thread != null) {
                    if (thread.shortTitle != null) {
                        val stations = thread.shortTitle.split(" — ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        domainThread.from = stations[0]
                        domainThread.to = stations[1]
                    }
                    if (thread.number != null) {
                        domainThread.comfortLevel = TrainComfortLevel.getComfortLevel(thread.number)
                    }
                }
                result.add(domainThread)
            }
            Collections.sort(result) { (_, departure1), (_, departure2) -> departure1.compareTo(departure2) }
        }
        return result
    }
}
