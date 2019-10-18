package com.a531tracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Layout;
import android.view.Gravity;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class ErrorAlerts extends AlertDialog {

    private String message;
    private String title;
    private boolean cancelable;
    private boolean negButton;
    private String extraMessageValue;
    private boolean failedLift;

    protected ErrorAlerts(Context context) {
        super(context);
    }

    public void setErrorAlertsValues(boolean setNeg, boolean cancelable, String title, String message, String extraMessage, boolean failedLift){
        setMessage(message);
        setTitle(title);
        setCancelable(cancelable);
        setNegButton(setNeg);
        setExtraMessageValue(extraMessage);
        setFailedLift(failedLift);
    }

    public AlertDialog createDialogAlert(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        builder.setMessage(getMessage())
                .setTitle(getTitle())
                .setCancelable(isCancelable())
                .setPositiveButton(R.string.ok_text, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        if(failedLift){
            TextView tv = new TextView(context);
            tv.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
            tv.setText(getExtraMessageValue());
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(20f);
            tv.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
            builder.setView(tv);
        }

        if(isNegButton()) {
            builder.setNegativeButton(R.string.cancel_text, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
        }

        return builder.create();
    }

    private void setMessage(String message){
        this.message = message;
    }

    private String getMessage(){
        return message;
    }

    private void setTitle(String title) {
        this.title = title;
    }

    private String getTitle() {
        return title;
    }

    @Override
    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }

    private boolean isCancelable() {
        return cancelable;
    }

    private void setNegButton(boolean negButton) {
        this.negButton = negButton;
    }

    private boolean isNegButton() {
        return negButton;
    }

    private void setExtraMessageValue(String extraMessageValue) {
        this.extraMessageValue = extraMessageValue;
    }

    private String getExtraMessageValue() {
        return extraMessageValue;
    }

    private void setFailedLift(boolean failedLift) {
        this.failedLift = failedLift;
    }
}
