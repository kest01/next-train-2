package ru.kest.trainswidget.model.domain;

import lombok.Data;

import java.util.Date;

/**
 * Created by KKharitonov on 07.01.2016.
 */
@Data
public class TrainThread {

    private Date arrival;
    private Date departure;
    private String title;

    private String from;
    private String to;

}
