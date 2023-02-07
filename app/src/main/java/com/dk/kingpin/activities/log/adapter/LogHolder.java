
package com.dk.kingpin.activities.log.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dk.kingpin.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class LogHolder extends RecyclerView.ViewHolder {

    public final RelativeLayout item;
    public final CircleImageView appIcon;
    public final ImageView logCamDot, logMicDot, logLocDot;
    public final TextView appName, appPackage, appTimestamp;
    public final View cameraStart, cameraStop, micStart, micStop, locStart, locStop;

    public LogHolder(View itemView) {
        super(itemView);
        item = itemView.findViewById(R.id.logItem);
        appIcon = itemView.findViewById(R.id.logAppIcon);
        appName = itemView.findViewById(R.id.logAppName);
        appPackage = itemView.findViewById(R.id.logAppPackage);
        appTimestamp = itemView.findViewById(R.id.logTimestamp);

        logCamDot = itemView.findViewById(R.id.logCameraDot);
        logMicDot = itemView.findViewById(R.id.logMicDot);
        logLocDot = itemView.findViewById(R.id.logLocDot);

        cameraStart = itemView.findViewById(R.id.lineCameraStart);
        cameraStop = itemView.findViewById(R.id.lineCameraStop);
        micStart = itemView.findViewById(R.id.lineMicStart);
        micStop = itemView.findViewById(R.id.lineMicStop);
        locStart = itemView.findViewById(R.id.lineLocStart);
        locStop = itemView.findViewById(R.id.lineLocStop);
    }
}
